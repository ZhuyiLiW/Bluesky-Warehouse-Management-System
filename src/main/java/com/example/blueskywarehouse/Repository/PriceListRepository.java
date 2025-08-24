package com.example.blueskywarehouse.Repository;

import com.example.blueskywarehouse.Dto.PriceListDto;
import com.example.blueskywarehouse.Entity.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {


    @Modifying
    @Query("""
    UPDATE PriceList p
    SET p.price = :price
    WHERE p.itemId = :itemId
""")
    void updatePriceList(@Param("itemId") int itemId,
                         @Param("price") String price);

    // PriceHistory Modul wird wahrscheinlich gelöscht werden
    @Modifying
    @Query(
            value = """
        INSERT INTO price_history (item_id, date, price)
        VALUES (:itemId, :date, :price)
        """,
            nativeQuery = true
    )
    void insertPriceListHistory(
            @Param("itemId") int itemId,
            @Param("price") double price,
            @Param("date") LocalDate date
    );




    @Query("""
    SELECT new com.example.blueskywarehouse.Dto.PriceListDto(
        p.itemId,
        i.name,
        p.price,
        p.remark
    )
    FROM PriceList p
    JOIN Item i ON p.itemId = i.id
""")
    List<PriceListDto> searchPriceList();

// PriceHistory Modul wird wahrscheinlich gelöscht werden
    @Query(
            value = """
        SELECT 
            p.id, 
            i.name AS item_name, 
            p.price, 
            p.date
        FROM price_history p
        JOIN items i ON p.item_id = i.id
        WHERE i.id = :itemId
        """,
            nativeQuery = true
    )
    List<PriceList> searchPriceHistory(@Param("itemId") int itemId);


    @Query("""
    SELECT p.id
    FROM PriceList p
    WHERE p.itemId = :itemId
""")
    List<Integer> checkItemId(@Param("itemId") int itemId);

}
