package com.example.blueskywarehouse.Repository;

import com.example.blueskywarehouse.Entity.AmazonProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmazonProductRepository extends JpaRepository<AmazonProduct, Long> {

    @Query("""
       SELECT a.newBarCode
       FROM AmazonProduct a
       WHERE a.oldBarCode = :oldBarCode
       """)
    String getNewBarCode(@Param("oldBarCode") String oldBarCode);

    @Query("""
       SELECT c.id
       FROM Customer c
       WHERE c.name = :customerName
       """)
    Integer getCustomerByName(@Param("customerName") String customerName);

    @Query("""
       SELECT a.weight
       FROM AmazonProduct a
       WHERE a.productName = :productName
       """)
    Double getWeight(@Param("productName") String productName);

}
