package com.example.blueskywarehouse.Repository;

import com.example.blueskywarehouse.Dto.AllStockShadow;
import com.example.blueskywarehouse.Dto.AllStockLocationDto;
import com.example.blueskywarehouse.Dto.BinCodeFromItemDto;
import com.example.blueskywarehouse.Entity.AllStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckStockInfoRepository extends JpaRepository<AllStock, Long> {
//coalesce 不强依赖数据库mysql,  pgp.priority NULLS LAST, i.id; 再创建一个表对比排序避免硬编码
    @Query(value = """
   SELECT
       i.id,
       i.name AS name,
       COALESCE(ROUND(SUM(p.box_stock), 2), 0) AS totalBoxStock,
       COALESCE(SUM(p.unit_stock), 0) AS totalUnitStock
   FROM
       items i
   JOIN
       pallet_info p ON i.id = p.item_id
   LEFT JOIN
       product_group_priority pgp ON i.product_group = pgp.group_name
   WHERE
       i.product_group IN (:groups) \s
   GROUP BY
       i.id, i.name, i.product_group, pgp.priority
   HAVING
       SUM(p.unit_stock) > 0
   ORDER BY
         (pgp.priority IS NULL), 
           pgp.priority,
           i.id
   
                  
            
""", nativeQuery = true) 
    List<AllStockShadow> getAllStock(@Param("groups") List<String> groups);
    //In JPQL wird GROUP_CONCAT standardmäßig nicht unterstützt. new com 只能在JPQL用
    @Query(value = """
    SELECT
        i.name AS name,
        GROUP_CONCAT(s.bin_code ORDER BY s.bin_code SEPARATOR ',\\n') AS location,
        CAST(SUM(p.box_stock) AS DOUBLE) AS total_box_stock,
        CAST(SUM(p.unit_stock) AS SIGNED)AS total_unit_stock
    FROM
        pallet_info p
    JOIN
        items i ON i.id = p.item_id
    JOIN
        storage_slot s ON s.pallet_id = p.id
    GROUP BY
        i.id, i.name
    ORDER BY
        i.name
    """, nativeQuery = true)
    List<AllStockLocationDto> getAllStockLocation();

    @Query( """
    SELECT 
      new com.example.blueskywarehouse.Dto.BinCodeFromItemDto(
           s.binCode,
           p.boxStock,
           p.unitStock,
           p.id
       )
    FROM 
        StorageSlot s
    JOIN 
         PalletInfo p  
     ON
     s.palletId=p.id
    WHERE 
        p.itemId = :itemId
    """)
    List<BinCodeFromItemDto> getAllBincodeByItemId(@Param("itemId")int itemId);


}
