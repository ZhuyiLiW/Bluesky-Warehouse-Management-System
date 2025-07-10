package com.example.blueskywarehouse.Dao;

import com.example.blueskywarehouse.Entity.AmazonProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AmazonProductRepository extends JpaRepository<AmazonProduct, Long> {

    @Query(
            value = "SELECT new_barcode from amazon_products where old_barcode=:oldBarCode",
            nativeQuery = true
    )
    String getNewBarCode(String oldBarCode);
    @Query(
            value = "SELECT id from customer where name=:customerName",
            nativeQuery = true
    )
    Integer getCustomerByName(String customerName);
    @Query(
            value = "SELECT weight from amazon_products where name=:customerName",
            nativeQuery = true
    )
    double getWeight(String customerName);
    @Query(
            value = "SELECT operation_id from amazon_products where customer_id=:customerId",
            nativeQuery = true
    )
    Integer getOperationNumber(int customerId);
}
