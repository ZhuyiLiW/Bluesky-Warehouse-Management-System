package com.example.blueskywarehouse.Repository;
import com.example.blueskywarehouse.Entity.AllStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PalletLayerRepository extends JpaRepository<AllStock, Long>{

    @Query("""
    SELECT COALESCE(SUM(COALESCE(p.unitStock, p.boxStock)), 0)
    FROM StorageSlot s
    JOIN PalletInfo p ON s.palletId=p.id
    WHERE s.binCode = :binCode AND p.itemId = :itemId
""")
    Integer getStock(@Param("itemId") int itemId, @Param("binCode") String binCode);
    @Query("""
    SELECT p.version
    FROM StorageSlot s
    JOIN PalletInfo p ON s.palletId=p.id
    WHERE s.binCode = :binCode AND p.itemId = :itemId
""")
    Integer getVersion(@Param("itemId") int itemId,@Param("binCode") String oldBinCode);

    @Modifying
    @Query(value = """
    UPDATE pallet_info p
    JOIN storage_slot s ON p.id = s.pallet_id
    JOIN items i ON i.id = p.item_id
    SET 
        p.unit_stock = CASE 
            WHEN p.unit_stock IS NOT NULL THEN :stock - :itemCount
            ELSE p.unit_stock 
        END,
        p.box_stock = CASE 
            WHEN p.unit_stock IS NULL THEN :stock - :itemCount
            WHEN p.unit_stock IS NOT NULL THEN ROUND((:stock - :itemCount) / i.units_per_box, 2)
            ELSE p.box_stock 
        END,
        p.version = p.version + 1
    WHERE p.item_id = :itemId
      AND s.bin_code = :binCode
      AND p.version = :version
    """, nativeQuery = true)
    int minusStock(
            @Param("itemId") int itemId,
            @Param("itemCount") int itemCount,
            @Param("binCode") String binCode,
            @Param("stock") int stock,
            @Param("version") int version
    );


    @Modifying
    @Query(value = """
    UPDATE pallet_info p
    JOIN storage_slot s ON s.pallet_id = p.id
    SET 
        p.item_id   = 49,
        p.box_stock = 0,
        p.unit_stock = 0,
        p.version = p.version + 1   -- 每次更新 version + 1
    WHERE p.item_id = :itemId
      AND s.bin_code = :binCode
      AND p.version = :version      -- 乐观锁条件
    """, nativeQuery = true)
    int changePalettStatusInto49(
            @Param("itemId") int itemId,
            @Param("binCode") String binCode,
            @Param("version") int version
    );



    @Modifying
    @Query(value = """
    UPDATE pallet_info p
    JOIN storage_slot s ON s.pallet_id = p.id
    JOIN items i ON i.id = p.item_id
    SET 
        p.unit_stock = CASE 
            WHEN p.unit_stock IS NOT NULL THEN :stock + :itemsCount
            ELSE p.unit_stock 
        END,
        p.box_stock = CASE 
            WHEN p.unit_stock IS NOT NULL THEN ROUND((:stock + :itemsCount) / i.units_per_box, 2)
            ELSE :stock + :itemsCount 
        END,
        p.version = p.version + 1
    WHERE p.item_id = :itemId
      AND s.bin_code = :binCode
      AND p.version = :version
    """, nativeQuery = true)
    int addStock(
            @Param("stock") int stock,
            @Param("itemId") int itemId,
            @Param("itemsCount") int itemsCount,
            @Param("binCode") String binCode,
            @Param("version") int version
    );




}
