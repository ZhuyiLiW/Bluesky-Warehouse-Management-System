package com.example.blueskywarehouse.Dao;

import com.example.blueskywarehouse.Entity.PalletInfo;
import com.example.blueskywarehouse.Entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PalletInfoRepository extends JpaRepository<PalletInfo, Long> {
}
