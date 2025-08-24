package com.example.blueskywarehouse.Repository;

import com.example.blueskywarehouse.Entity.AmazonRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmazonRefundRepository extends JpaRepository<AmazonRefund, Long> {
}
