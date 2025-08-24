package com.example.blueskywarehouse.Repository;

import com.example.blueskywarehouse.Dto.ItemCountDto;
import com.example.blueskywarehouse.Entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemManagementRepository extends JpaRepository<Item, Long> {

    @Query("""
    SELECT i 
    FROM Item i 
    WHERE i.name LIKE CONCAT('%', :name, '%') 
       OR CAST(i.id AS string) = :name
""")
    List<Item> searchItem(@Param("name") String name);



    @Query("""
    select s.binCode from StorageSlot s JOIN PalletInfo p ON s.palletId=p.id where p.itemId=:itemId
""")
    List<String> searchItemLocation(@Param("itemId")int itemId);

    @Query("""
 select new com.example.blueskywarehouse.Dto.ItemCountDto (
    SUM(p.boxStock),
    COALESCE(SUM(p.unitStock), 0)
 )
 from PalletInfo p where p.itemId = :itemId
""")
    ItemCountDto searchItemCount(@Param("itemId")int itemId);


}
