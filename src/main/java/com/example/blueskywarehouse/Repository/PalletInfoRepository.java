package com.example.blueskywarehouse.Repository;

import com.example.blueskywarehouse.Dto.AllItemFromBinDto;
import com.example.blueskywarehouse.Entity.PalletInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PalletInfoRepository extends JpaRepository<PalletInfo, Long> {
    @Modifying
    @Query(" delete from PalletInfo where itemId =49 ")
    void deleteItemId49();

    @Modifying
    @Query(value = "delete from pallet_info where id in (select pallet_id from storage_slot where bin_code=:binCode)",nativeQuery = true)
    void deleteAllPalettFromBin(@Param("binCode") String binCode);
// DTO类型要严格和一下括号内参数一样
@Query("""
    SELECT new com.example.blueskywarehouse.Dto.AllItemFromBinDto(
        i.id,
        i.name,
        p.boxStock,
        p.unitStock
    )
    FROM StorageSlot s
    LEFT JOIN PalletInfo p on s.palletId = p.id
    LEFT JOIN Item i on p.itemId=i.id
    WHERE s.binCode = :binCode
""")
List<AllItemFromBinDto> searchAllItemFromBin(@Param("binCode") String binCode);



}
