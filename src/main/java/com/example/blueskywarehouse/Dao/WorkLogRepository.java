package com.example.blueskywarehouse.Dao;

import com.example.blueskywarehouse.Dto.CustomerRecord;
import com.example.blueskywarehouse.Entity.WorkLog;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    @Query(value = "SELECT * FROM inventory_operations WHERE customer_name = :content", nativeQuery = true)
    List<WorkLog> findByContentContainingIgnoreCaseNative(@Param("content") String content);

    @Modifying
    @Transactional

    @Query(value = "insert into inventory_operations (customer_name, operation_date, item_id, items_count, status, bin_code) " +
            "VALUES (:customerName, :operationDate, :itemId, :itemsCount, :status, :binCode)", nativeQuery = true)
    void insertWorklog(@Param("customerName") String customerName,
                       @Param("operationDate") LocalDateTime operationDate,
                       @Param("itemId") int itemId,
                       @Param("itemsCount") int itemsCount,
                       @Param("status") int status,
                       @Param("binCode") String binCode);

    @Query(value = "SELECT " +
            "sum(unit_stock) AS stock " +
            "FROM pallet_info p " +
            "JOIN storage_slot s ON p.id = s.pallet_id " +
            "WHERE s.bin_code = :binCode AND p.item_id = :itemId",
            nativeQuery = true)
    Integer getStock(@Param("itemId") int itemId, @Param("binCode") String binCode);

    @Modifying
    @Transactional
    @Query(value = "UPDATE " +
            "pallet_info p " +
            "JOIN storage_slot s ON p.id = s.pallet_id " +
            "JOIN items i ON i.id = p.item_id " +
            "SET " +
            "p.unit_stock = CASE " +
            "  WHEN p.unit_stock IS NOT NULL THEN :stock - :itemCount " +
            "  ELSE p.unit_stock " +
            "END, " +
            "p.box_stock = CASE " +
            "  WHEN p.unit_stock IS NOT NULL AND i.units_per_box != 0 THEN ROUND((:stock - :itemCount) / i.units_per_box, 2) " +
            "  WHEN p.unit_stock IS NOT NULL AND i.units_per_box = 0 THEN 0 " +
            "  ELSE p.box_stock " +
            "END " +
            "WHERE p.item_id = :itemId AND s.bin_code = :binCode",
            nativeQuery = true)
    void minusStock(
            @Param("itemId") int itemId,
            @Param("itemCount") int itemCount,
            @Param("binCode") String binCode,
            @Param("stock") int stock
    );


    @Modifying
    @Transactional
    @Query(value = "UPDATE " +
            "pallet_info p " +
            "JOIN storage_slot s ON s.pallet_id = p.id " +
            "SET " +
            "p.item_id = 49, " +
            "p.box_stock = 0, " +
            "p.unit_stock = 0 " +
            "WHERE " +
            "p.item_id = :itemId " +
            "AND s.bin_code = :binCode",
            nativeQuery = true)
    void changePalettStatusInto49(@Param("itemId") int itemId,
                                  @Param("binCode") String binCode);

    @Modifying
    @Transactional
    @Query(value = "UPDATE " +
            "pallet_info p " +
            "JOIN storage_slot s ON s.pallet_id = p.id " +
            "JOIN items i ON i.id = p.item_id " +
            "SET " +
            "p.unit_stock = CASE " +
            "  WHEN p.unit_stock IS NOT NULL THEN :stock + :itemsCount " +
            "  ELSE p.unit_stock " +
            "END, " +
            "p.box_stock = CASE " +
            "  WHEN p.unit_stock IS NOT NULL THEN ROUND((:stock + :itemsCount) / NULLIF(i.units_per_box, 0), 2) " +
            "  ELSE :stock + :itemsCount " +
            "END " +
            "WHERE p.item_id = :itemId AND s.bin_code = :binCode",
            nativeQuery = true)
    void addStock(@Param("stock") int stock,
                  @Param("itemId") int itemId,
                  @Param("itemsCount") int itemsCount,
                  @Param("binCode") String binCode);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO pallet_info(item_id, unit_stock, box_stock) " +
            "VALUES (:itemId, :itemsCount, " +
            "ROUND(:itemsCount / NULLIF((SELECT units_per_box FROM items WHERE id = :itemId), 0), 2))",
            nativeQuery = true)
    void insertStockPalett(@Param("itemId") int itemId,
                           @Param("itemsCount") int itemsCount,
                           @Param("binCode") String binCode);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO storage_slot (pallet_id, bin_code, slot_code, is_available) " +
            "VALUES ( " +
            "    (SELECT MAX(id) FROM pallet_info), " +
            "    :binCode, " +
            "    SUBSTRING_INDEX(:binCode, '-', 1), " +
            "    1 " +
            ")",
            nativeQuery = true)
    void insertStockBin(@Param("binCode") String binCode);

    @Modifying
    @Transactional
    @Query(value = " delete from pallet_info where id=:palettId", nativeQuery = true)
    void deleteItemId(Integer palettId);

    @Query(value = "SELECT * \n" +
            "FROM inventory_operations \n" +
            "WHERE id not in (SELECT id\n" +
            "FROM inventory_operations \n" +
            "WHERE remarks like '%deleted%') and id=:worklogId;", nativeQuery = true)
    WorkLog getWorkLogById(int worklogId);

    @Query(value = " SELECT sum(unit_stock)   from  pallet_info p join storage_slot s on p.id=s.pallet_id where p.item_id=:itemId and  s.bin_code = :binCode ;", nativeQuery = true)
    Double getUnitstockFromWorklog(int itemId, String binCode);

    @Modifying
    @Transactional
    @Query(value = "UPDATE pallet_info p " +
            "JOIN storage_slot s ON p.id = s.pallet_id " +
            "SET " +
            "    p.unit_stock = :aktuellCount + :rollbackCount, " +
            "    p.box_stock = (:aktuellCount + :rollbackCount) / " +
            "        NULLIF((SELECT i.units_per_box FROM items i WHERE i.id = :itemId), 0) " +
            "WHERE " +
            "    p.item_id = :itemId " +
            "    AND s.bin_code = :binCode", nativeQuery = true)
    void rollbackWorklog0(double aktuellCount, double rollbackCount, int itemId, String binCode);

    @Modifying
    @Transactional
    @Query(value = "UPDATE\n" +
            " pallet_info p\n" +
            "JOIN storage_slot s ON p.id = s.pallet_id\n" +
            "SET \n" +
            "    p.unit_stock = :aktuellCount - :rollbackCount,\n" +
            "    p.box_stock = (:aktuellCount - :rollbackCount) / (\n" +
            "        SELECT NULLIF(i.units_per_box, 0) FROM items i WHERE i.id = :itemId\n" +
            "    )\n" +
            "WHERE \n" +
            "    p.item_id = :itemId\n" +
            "    AND s.bin_code = :binCode",
            nativeQuery = true)
    void rollbackWorklog1(@Param("aktuellCount") double aktuellCount,
                          @Param("rollbackCount") double rollbackCount,
                          @Param("itemId") int itemId,
                          @Param("binCode") String binCode);


    @Modifying
    @Transactional
    @Query(value = " Update inventory_operations set remarks ='deleted' where id =:id", nativeQuery = true)
    void worklogExpired(int id);


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
    @Transactional
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
    @Transactional
    @Query(value = """
    DELETE  FROM storage_slot where pallet_id=:palettId 
""", nativeQuery = true)
    void deletePalettFromBin(@Param("palettId") int palettId);

    @Query(value = "  SELECT p.id\n" +
            "    FROM pallet_info p\n" +
            "    JOIN storage_slot s ON s.pallet_id = p.id\n" +
            "    WHERE p.item_id = :itemId AND s.bin_code = :binCode\n" +
            "    LIMIT 1  ", nativeQuery = true)
    Integer getPalettId(@Param("itemId")int itemId, @Param("binCode")String binCode);

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
SELECT * from  inventory_operations where operation_date BETWEEN :startDate and :endDate and (remarks IS NULL OR remarks <> 'deleted')
                        
    """, nativeQuery = true)
    List<WorkLog> getWorklistByPeriode(String startDate, String endDate);
    @Query(value = """
SELECT * from  inventory_operations where operation_date BETWEEN :startDate and :endDate and customer_name LIKE CONCAT('%', :customerName, '%') and (remarks IS NULL OR remarks <> 'deleted')
                        
    """, nativeQuery = true)
    List<WorkLog> getWorklistByPeriodeAndCustomerName(String startDate, String endDate, String customerName);

    @Query(value = """
SELECT * from  inventory_operations where operation_date BETWEEN :startDate and :endDate and item_id=:itemId and (remarks IS NULL OR remarks <> 'deleted')
                        
    """, nativeQuery = true)
    List<WorkLog> getWorklistByPeriodeAndItemId(String startDate, String endDate, int itemId);
    @Query(value = """
SELECT * from  inventory_operations where operation_date BETWEEN :startDate and :endDate and item_id=:itemId and customer_name LIKE CONCAT('%', :customerName, '%') and (remarks IS NULL OR remarks <> 'deleted')
                        
    """, nativeQuery = true)
    List<WorkLog> getWorklistByPeriodeAndItemIdAndCName(String startDate, String endDate, int itemId, String customerName);
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
    List<CustomerRecord> findByDateBetween(@Param("start") String start, @Param("end") String end);
    @Modifying
    @Transactional
    @Query(value = """
    insert into pallet_info (item_id, box_stock, unit_stock)
    VALUES (:itemId, ROUND(:itemsCount / NULLIF((SELECT units_per_box FROM items WHERE id = :itemId), 0), 2), :itemsCount)
    """, nativeQuery = true)
    void creatNewPalettForRollback(int itemsCount, int itemId);

    @Query(value = """
    select id from pallet_info order by id desc limit 1
    """, nativeQuery = true)
    Integer getPalettForRollback();

    @Modifying
    @Transactional
    @Query(value = """
    insert into storage_slot (bin_code, slot_code, pallet_id, is_available)
    values (:binCode, :slotCode, :newPalettId, 1);
    """, nativeQuery = true)
    void insertNewPalettIntoBin(Integer newPalettId, String slotCode, String binCode);
    @Modifying
    @Transactional
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