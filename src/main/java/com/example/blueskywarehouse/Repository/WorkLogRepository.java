package com.example.blueskywarehouse.Repository;

import com.example.blueskywarehouse.Dto.CustomerDto;
import com.example.blueskywarehouse.Entity.WorkLog;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    @Query( "SELECT w FROM WorkLog w WHERE w.customerName  = :content")
    List<WorkLog> findByContentContainingIgnoreCaseNative(@Param("content") String content);

    @Query("""
       SELECT SUM(p.unitStock) AS stock
       FROM PalletInfo p
       JOIN StorageSlot s ON p.id = s.palletId
       WHERE s.binCode = :binCode
         AND p.itemId  = :itemId
       """)
    Integer getStock(@Param("itemId") int itemId,
                     @Param("binCode") String binCode);


    @Modifying
    @Query(value = """
       UPDATE pallet_info p
       JOIN storage_slot s ON p.id = s.pallet_id
       JOIN items i        ON i.id = p.item_id
       SET
           p.unit_stock = CASE
                             WHEN p.unit_stock IS NOT NULL
                               THEN :stock - :itemCount
                             ELSE p.unit_stock
                          END,
           p.box_stock  = CASE
                             WHEN p.unit_stock IS NOT NULL AND i.units_per_box != 0
                               THEN ROUND((:stock - :itemCount) / i.units_per_box, 2)
                             WHEN p.unit_stock IS NOT NULL AND i.units_per_box = 0
                               THEN 0
                             ELSE p.box_stock
                          END
       WHERE p.item_id  = :itemId
         AND s.bin_code = :binCode
       """,
            nativeQuery = true)
    void minusStock(
            @Param("itemId") int itemId,
            @Param("itemCount") int itemCount,
            @Param("binCode") String binCode,
            @Param("stock") int stock
    );


    @Modifying
    @Query(value = """
       UPDATE pallet_info p
       JOIN storage_slot s ON s.pallet_id = p.id
       JOIN items i        ON i.id = p.item_id
       SET 
           p.unit_stock = CASE
                              WHEN p.unit_stock IS NOT NULL 
                                THEN :stock + :itemsCount
                              ELSE p.unit_stock
                          END,
           p.box_stock  = CASE
                              WHEN p.unit_stock IS NOT NULL 
                                THEN ROUND((:stock + :itemsCount) / NULLIF(i.units_per_box, 0), 2)
                              ELSE :stock + :itemsCount
                          END
       WHERE p.item_id  = :itemId
         AND s.bin_code = :binCode
       """,
            nativeQuery = true)
    void addStock(@Param("stock") int stock,
                  @Param("itemId") int itemId,
                  @Param("itemsCount") int itemsCount,
                  @Param("binCode") String binCode);


    @Modifying
    @Query(value = """
    INSERT INTO pallet_info (item_id, unit_stock, box_stock)
    VALUES (
        :itemId,
        :itemsCount,
        ROUND(
            :itemsCount / NULLIF(
                (SELECT units_per_box 
                   FROM items 
                  WHERE id = :itemId), 
                0
            ), 
            2
        )
    )
    """, nativeQuery = true)
    void insertStockPalett(@Param("itemId") int itemId,
                           @Param("itemsCount") int itemsCount,
                           @Param("binCode") String binCode);


    @Modifying
    @Query(value = """
    INSERT INTO storage_slot (
        pallet_id,
        bin_code,
        slot_code,
        is_available
    )
    VALUES (
        (SELECT MAX(id) FROM pallet_info),
        :binCode,
        SUBSTRING_INDEX(:binCode, '-', 1),
        1
    )
    """, nativeQuery = true)
    void insertStockBin(@Param("binCode") String binCode);


    @Modifying
    @Query( " delete from PalletInfo p where p.id=:palettId")
    void deleteItemId(@Param("palettId")Integer palettId);

    @Query(value = """
    SELECT *
    FROM inventory_operations
    WHERE id NOT IN (
        SELECT id
        FROM inventory_operations
        WHERE remarks LIKE '%deleted%'
    )
      AND id = :worklogId
    """, nativeQuery = true)
    WorkLog getWorkLogById(@Param("worklogId") int worklogId);


    @Query(value = " SELECT sum(unit_stock)   from  pallet_info p join storage_slot s on p.id=s.pallet_id where p.item_id=:itemId and  s.bin_code = :binCode ;", nativeQuery = true)
    Double getUnitstockFromWorklog(int itemId, String binCode);

    @Modifying
    @Query(value = """
       UPDATE pallet_info p
       JOIN storage_slot s ON p.id = s.pallet_id
       SET 
           p.unit_stock = :aktuellCount + :rollbackCount,
           p.box_stock  = (:aktuellCount + :rollbackCount) /
                          NULLIF(
                              (SELECT i.units_per_box 
                               FROM items i 
                               WHERE i.id = :itemId), 
                              0
                          )
       WHERE p.item_id  = :itemId
         AND s.bin_code = :binCode
       """,
            nativeQuery = true)
    void rollbackWorklog0(@Param("aktuellCount") double aktuellCount,
                          @Param("rollbackCount") double rollbackCount,
                          @Param("itemId") int itemId,
                          @Param("binCode") String binCode);

    @Modifying
    @Query(value = """
       UPDATE pallet_info p
       JOIN storage_slot s ON p.id = s.pallet_id
       SET 
           p.unit_stock = :aktuellCount - :rollbackCount,
           p.box_stock  = (:aktuellCount - :rollbackCount) /
                          (SELECT NULLIF(i.units_per_box, 0) 
                             FROM items i 
                            WHERE i.id = :itemId)
       WHERE p.item_id  = :itemId
         AND s.bin_code = :binCode
       """,
            nativeQuery = true)
    void rollbackWorklog1(@Param("aktuellCount") double aktuellCount,
                          @Param("rollbackCount") double rollbackCount,
                          @Param("itemId") int itemId,
                          @Param("binCode") String binCode);


    @Modifying
    @Query(value = " Update inventory_operations set remarks ='deleted' where id =:id", nativeQuery = true)
    void worklogExpired(@Param("id")int id);


    @Query(value = """
    SELECT s.bin_code
    FROM pallet_info p
    JOIN storage_slot s ON p.id = s.pallet_id
    WHERE p.item_id = :itemId
    ORDER BY 
        -- 优先显示 bin_code 最后一段是 '1' 的
        RIGHT(SUBSTRING_INDEX(s.bin_code, '-', -1), 1) = '1' DESC,
        p.unit_stock ASC
    """, nativeQuery = true)
    List<String> findOptimalBin(@Param("itemId") int itemId);


    @Modifying
    @Query(value = """
    DELETE FROM storage_slot
    WHERE bin_code = :binCode
      AND NOT EXISTS (
          SELECT * FROM (
              SELECT 1 FROM storage_slot
              WHERE bin_code = :binCode AND pallet_id IS NOT NULL
          ) AS temp
      )  ;
""", nativeQuery = true)
    void deleteEmptyBin(@Param("binCode") String binCode);

    @Modifying
    @Query( """
    DELETE  FROM StorageSlot s where s.palletId=:palettId 
""")
    void deletePalettFromBin(@Param("palettId") int palettId);

    @Query(value = """
    SELECT p.id
    FROM pallet_info p
    JOIN storage_slot s 
      ON s.pallet_id = p.id
    WHERE p.item_id = :itemId
      AND s.bin_code = :binCode
    LIMIT 1
    """, nativeQuery = true)
    Integer getPalettId(@Param("itemId") int itemId,
                        @Param("binCode") String binCode);


    @Query(value = """
    SELECT IFNULL(SUM(p.unit_stock), 0)
    FROM pallet_info p
    JOIN storage_slot s ON s.pallet_id = p.id
    WHERE p.item_id = :itemId
    """, nativeQuery = true)
    Integer getAllStockCount(@Param("itemId") int itemId);

    @Query(value = """
    SELECT\s
                            io.id as id,
                            customer_name as customer_name,
                            operation_date as operation_date ,
                            i.name as item_name,\s
                            io.items_count as items_count,
                            io.status AS status,
                            io.bin_code as bin_code,
                            remarks as remarks,
                            i.id
                           
                        FROM \s
                            inventory_operations io
                        JOIN\s
                            items i ON io.item_id = i.id
                        WHERE\s
                            io.operation_date LIKE CONCAT('%',:date,'%')
                            and remarks IS NULL || remarks <> 'deleted';
                        
    """, nativeQuery = true)
    List<Object[] > getWorkLogByDate(String date);

    @Query(value = """
    SELECT w
    FROM WorkLog w
    WHERE w.operationDate BETWEEN :startDate AND :endDate
      AND (w.remarks IS NULL OR w.remarks <> 'deleted')
    """,
            countQuery = """
    SELECT COUNT(w)
    FROM WorkLog w
    WHERE w.operationDate BETWEEN :startDate AND :endDate
      AND (w.remarks IS NULL OR w.remarks <> 'deleted')
    """)
    Page<WorkLog> getWorklistByPeriode(
            Timestamp startDate,
            Timestamp endDate,
            Pageable pageable
    );


    @Query("""
    SELECT w
    FROM WorkLog w
    WHERE w.operationDate BETWEEN :startDate AND :endDate
      AND w.customerName LIKE CONCAT('%', :customerName, '%')
      AND (w.remarks IS NULL OR w.remarks <> 'deleted')
    """)
    List<WorkLog> getWorklistByPeriodeAndCustomerName(
            Timestamp startDate,
            Timestamp endDate,
            String customerName
    );



    @Query("""
    SELECT w
    FROM WorkLog w
    WHERE w.operationDate BETWEEN :startDate AND :endDate
      AND w.itemId = :itemId
      AND (w.remarks IS NULL OR w.remarks <> 'deleted')
    """)
    List<WorkLog> getWorklistByPeriodeAndItemId(
            Timestamp startDate,
            Timestamp endDate,
            int itemId
    );

    @Query("""
    SELECT w
    FROM WorkLog w
    WHERE w.operationDate BETWEEN :startDate AND :endDate
      AND w.itemId = :itemId
      AND w.customerName LIKE CONCAT('%', :customerName, '%')
      AND (w.remarks IS NULL OR w.remarks <> 'deleted')
    """)
    List<WorkLog> getWorklistByPeriodeAndItemIdAndCName(
            Timestamp startDate,
            Timestamp endDate,
            int itemId,
            String customerName
    );

    @Query(value = """
    SELECT
        ROW_NUMBER() OVER (ORDER BY io.operation_date) AS `Arbeitsnummer`,
        io.customer_name AS `Lieferadresse`,
        i.name AS `Waren`,
        io.items_count AS `Zahl`,
        CASE
            WHEN io.status = 0 THEN 'Versand'
            ELSE 'Eingang'
        END AS `Remark`,
        DATE(io.operation_date) AS date
      
    FROM
        inventory_operations io
    JOIN
        items i ON i.id = io.item_id
    WHERE
        io.operation_date BETWEEN :start AND CONCAT(:end, ' 23:59:59')
        AND (remarks != 'deleted' OR remarks IS NULL)
    ORDER BY
        io.customer_name,io.status
    """, nativeQuery = true)
    List<CustomerDto> findByDateBetween(@Param("start") Timestamp start, @Param("end") Timestamp end);



    @Modifying
    @Query(value = """
DELETE FROM pallet_info
WHERE id IN (
    SELECT id FROM (
        SELECT p.id
        FROM pallet_info p
        JOIN storage_slot s ON p.id = s.pallet_id
        WHERE p.item_id = :itemId AND s.bin_code = :binCode
    ) AS temp_ids
);
    """, nativeQuery = true)
    void deletePalettByItemId(int itemId, String binCode);
}

