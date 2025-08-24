package com.example.blueskywarehouse.Repository;

import com.example.blueskywarehouse.Entity.StorageSlot;
import com.example.blueskywarehouse.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StorageSlotRepository extends JpaRepository<StorageSlot, Long> {
    @Modifying
    @Query("delete from  StorageSlot where binCode=:binCode")
    void deleteBin(@Param("binCode") String binCode);
    @Modifying
    @Query(value = """
    INSERT INTO storage_slot (pallet_id, bin_code, slot_code, is_available)
    VALUES (
        (SELECT MAX(id) FROM pallet_info),
        :binCode,
        SUBSTRING_INDEX(:binCode, '-', 1),
        1
    )
    """, nativeQuery = true)
    void insertStockBin(@Param("binCode") String binCode);

}
