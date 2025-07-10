package com.example.blueskywarehouse.Dao;
import com.example.blueskywarehouse.Entity.AllStock;
import com.example.blueskywarehouse.Entity.WorkLog;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface PalletLayerRepository extends JpaRepository<AllStock, Long>{

    @Query(value = "SELECT " +
            "sum(IFNULL(p.unit_stock, p.box_stock)) AS stock " +
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
            "  WHEN p.unit_stock IS NULL THEN :stock - :itemCount " +
            "  WHEN p.unit_stock IS NOT NULL THEN ROUND((:stock - :itemCount) / i.units_per_box, 2) " +
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
            "  WHEN p.unit_stock IS NOT NULL THEN ROUND((:stock + :itemsCount) / i.units_per_box, 2) " +
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
    @Query(value = "INSERT INTO pallet_info(item_id, unit_stock, box_stock)\n" +
            "VALUES (\n" +
            "    :itemId,\n" +
            "    :itemsCount,\n" +
            "    ROUND(:itemsCount / NULLIF((SELECT units_per_box FROM items WHERE id = :itemId), 0), 2)\n" +
            ")\n",
            nativeQuery = true)

    void insertStockPalett(int itemId, int itemsCount, String binCode);
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
    @Query(value = " delete from pallet_info where item_id =49 ",nativeQuery = true)
    void deleteItemId49();

    @Modifying
    @Transactional
    @Query(value = "delete from pallet_info where id in (select pallet_id from storage_slot where bin_code=:binCode)",nativeQuery = true)
    void deleteAllPalettFromBin(String binCode);
    @Modifying
    @Transactional
    @Query(value = "delete from  storage_slot  where bin_code=:binCode",nativeQuery = true)
    void deleteBin(String binCode);

    @Query(value = """
    SELECT 
        pallet_info.item_id, 
        i.name, 
        pallet_info.box_stock, 
        pallet_info.unit_stock
    FROM pallet_info 
    JOIN storage_slot ON pallet_info.id = storage_slot.pallet_id 
    JOIN items i ON i.id = pallet_info.item_id 
    WHERE storage_slot.bin_code = :binCode
""", nativeQuery = true)
    List<Object[]> searchAllItemFromBin( String binCode);



}
