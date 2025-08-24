package com.example.blueskywarehouse.Repository;

import com.example.blueskywarehouse.Entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OptimalStorageLocationRepository extends JpaRepository<WorkLog, Long> {
    // Limit 1 bei Postgre SQL und Oracel ist FETCH FIRST 1 ROWS ONLY
    // count 只是行数 所以这里用sum最合适
    @Query(value = """
    SELECT s.slot_code
    FROM storage_slot s
    JOIN pallet_info p ON s.pallet_id = p.id
    WHERE p.item_id = :itemId
    GROUP BY s.slot_code
    ORDER BY COALESCE(SUM(p.unit_stock),0) DESC  LIMIT 1
    """, nativeQuery = true)
    String getOptimalSlot(@Param("itemId") int itemId);
    @Query(value = """
    SELECT bin_name 
    FROM all_bin_info 
    WHERE bin_name NOT IN (SELECT bin_code FROM storage_slot)
      AND bin_name LIKE CONCAT('%', :optimalSlot, '%')
    """, nativeQuery = true)
    List<String> getOptimalBinlist(@Param("optimalSlot") String optimalSlot);
    @Query(value = """
 SELECT s.slot_code
 FROM pallet_info p
 JOIN storage_slot s ON s.pallet_id = p.id
 WHERE p.item_id = :itemId
 GROUP BY s.slot_code
 ORDER BY SUM(p.unit_stock) DESC
 LIMIT 1;
 
    """, nativeQuery = true)
    String getOneOptimalSlot(@Param("itemId")int itemId);
//在 SQL 中，NOT IN 对 NULL 值比较敏感，如果 storage_slot.bin_code 里有 NULL，可能导致整个结果为空。
    @Query(value = """
    SELECT bin_name
    FROM all_bin_info
    WHERE bin_name NOT IN (
        SELECT bin_code FROM storage_slot
    )
    AND bin_name LIKE CONCAT(:optimalSlot, '%')
    """, nativeQuery = true)
    List<String> getAllEmptyBinListBySlot(@Param("optimalSlot") String optimalSlot);

    @Modifying
    @Query("""
  UPDATE StorageSlot s
  SET s.binCode = :newBinCode, 
      s.slotCode = :newSlotCode
  WHERE s.binCode = :oldBinCode
""")
    void InsertPalletsIntoNewBin(
            @Param("newBinCode") String newBinCode,
            @Param("newSlotCode") String newSlotCode,
            @Param("oldBinCode") String oldBinCode
    );
}
