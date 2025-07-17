package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.AmazonRefundRepository;
import com.example.blueskywarehouse.Dao.CheckStockInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AmazonRefundService {

    @Autowired
    AmazonRefundRepository amazonRefundRepository;
    Logger logger = LoggerFactory.getLogger(CheckStockInfoService.class);
}
