package com.example.blueskywarehouse.Dao;

import com.example.blueskywarehouse.Entity.AllStock;
import com.example.blueskywarehouse.Entity.AmazonRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmazonRefundRepository extends JpaRepository<AmazonRefund, Long> {
}
