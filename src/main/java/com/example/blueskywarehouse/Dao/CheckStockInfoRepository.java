package com.example.blueskywarehouse.Dao;

import com.example.blueskywarehouse.Entity.AllStock;
import com.example.blueskywarehouse.Entity.PalletInfo;
import com.example.blueskywarehouse.Entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CheckStockInfoRepository extends JpaRepository<AllStock, Long> {

    @Query(value = """
   SELECT\s
                      i.id,
                      i.name AS name,
                      ROUND(IFNULL(SUM(p.box_stock), 0), 2) AS total_box_stock,
                      IFNULL(SUM(p.unit_stock), 0) AS total_unit_stock,
                      0 as version
                  FROM
                      items i
                  JOIN
                      pallet_info p ON i.id = p.item_id
                  WHERE
                      i.product_group IN ('corona test','mask','paper bag','solar panel','Huawei','powerway Set','Liu','APS','Anker','BC','RJW','5PAIRS BAG','solar cabel','rail','bluesky','deepblue')  
                  GROUP BY
                      i.id, i.name, i.product_group
                      HAVING total_unit_stock>0
                  ORDER BY
                      CASE
                          WHEN i.product_group = 'Huawei' THEN 4
                          WHEN i.product_group = 'Anker' THEN 5
                          WHEN i.product_group = 'corona test' THEN 1
                          WHEN i.product_group = 'mask' THEN 2
                          WHEN i.product_group = 'paper bag' THEN 3
                          WHEN i.product_group = 'solar panel' THEN 6
                          WHEN i.product_group = 'BC' THEN 7
                          WHEN i.product_group = 'APS' THEN 8
                          WHEN i.product_group = 'Liu' THEN 88
                          ELSE 9
                      END,
                      i.id;
                  
            
""", nativeQuery = true)
    List<AllStock> getAllStock();
    @Query(value = """
    SELECT
        
        i.name AS name,
        GROUP_CONCAT(s.bin_code ORDER BY s.bin_code SEPARATOR ',\\n') AS location,
        SUM(p.box_stock) AS total_box_stock,
        SUM(p.unit_stock) AS total_unit_stock,
        0 as version
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
    List<Object[]> getAllStockLocation();

    @Query(value = """
    SELECT 
        s.bin_code AS name, 
        p.box_stock AS box_stock, 
        p.unit_stock AS unit_stock,
        p.id as id ,
         0 as version
    FROM 
        pallet_info p
    JOIN 
        storage_slot s ON p.id = s.pallet_id
    WHERE 
        p.item_id = :itemId
    """, nativeQuery = true)
    List<String> getAllBincodeByItemId(int itemId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM pallet_info where id=:id",
            nativeQuery = true
    )
    void deletePalletinfoById(int id);
}
