package com.example.blueskywarehouse.Dao;

import com.example.blueskywarehouse.Entity.AllStock;
import com.example.blueskywarehouse.Entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ItemManagementRepository extends JpaRepository<Item, Long> {
    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO items(name, type, units_per_box, product_group)
    VALUES (:name, :type, :unitPerBox, :productGroup)
""", nativeQuery = true)
    void addItem(
           String name, String type, Integer unitPerBox, String productGroup
    );

    @Query(value = """
    select * from items where name like concat('%',:name,'%') or id=:name
""", nativeQuery = true)
    List<Item> searchItem(String name);

    @Query(value = """
    select bin_code from pallet_info p JOIN storage_slot s on p.id=s.pallet_id and p.item_id=:itemId
""", nativeQuery = true)
    List<String> searchItemLocation(int itemId);

    @Query(value = """
     select  SUM(box_stock),SUM(unit_stock) from pallet_info where item_id=:itemId
                                                                                                    
""", nativeQuery = true)
    Object[] searchItemCount(int itemId);

}
