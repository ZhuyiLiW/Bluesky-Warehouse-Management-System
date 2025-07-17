package com.example.blueskywarehouse.Dao;

import com.example.blueskywarehouse.Entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface OptimalStorageLocationRepository extends JpaRepository<WorkLog, Long> {
    @Query(value = """
    SELECT s.slot_code
    FROM storage_slot s
    JOIN pallet_info p ON s.pallet_id = p.id
    WHERE p.item_id = :itemId
    GROUP BY s.slot_code
    ORDER BY COUNT(p.unit_stock) DESC  LIMIT 1
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
 ORDER BY  COUNT(*) DESC
 LIMIT 1;
 
    """, nativeQuery = true)
    String getOneOptimalSlot(int itemId);

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
    @Transactional
    @Query(value = "update storage_slot set bin_code=:newBinCode , slot_code=:newSlotCode where bin_code=:oldBinCode",nativeQuery = true)
    void InsertPalletsIntoNewBin(String newBinCode,String newSlotCode,String oldBinCode);
}
