package com.example.blueskywarehouse.Dao;

import com.example.blueskywarehouse.Entity.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO price_list (item_id, price,remark) VALUES (:itemId, :price, :remark)",
            nativeQuery = true
    )
    void insertPriceList(@Param("itemId") int itemId, @Param("price") double price, @Param("remark") String remark);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE price_list SET price = :price WHERE item_id = :itemId",
            nativeQuery = true
    )
    void updatePriceList(@Param("itemId") int itemId, @Param("price") double price);

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO price_history (item_id, date, price) " +
                    "VALUES (:itemId, :date, :price)",
            nativeQuery = true
    )
    void insertPriceListHistory(
            @Param("itemId") int itemId,
            @Param("price") double price,
            @Param("date") LocalDate date
    );

    @Query(
            value = "SELECT p.item_id AS id, i.name AS item_name, p.price,p.remark,0 as version " +
                    "FROM price_list p " +
                    "JOIN items i ON p.item_id = i.id",
            nativeQuery = true
    )
    List<PriceList> searchPriceList();

    @Query(
            value = "SELECT p.id, i.name AS item_name, p.price, p.date,0 as version  " +
                    "FROM price_history p " +
                    "JOIN items i ON p.item_id = i.id " +
                    "WHERE i.id = :itemId",
            nativeQuery = true
    )
    List<PriceList> searchPriceHistory(@Param("itemId") int itemId);

    @Query(
            value = "SELECT id  " +
                    "FROM price_list " +

                    "WHERE item_id = :itemId",
            nativeQuery = true
    )
    List<Integer> checkItemId(int itemId);
}
