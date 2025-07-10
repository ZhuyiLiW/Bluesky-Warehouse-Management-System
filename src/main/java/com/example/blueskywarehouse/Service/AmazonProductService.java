package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.AmazonProductRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AmazonProductService {
    @Autowired
    private AmazonProductRepository amazonProductRepository;

    Logger logger = LoggerFactory.getLogger(ItemManagementService.class);

    /**
     * 添加一个新的产品信息。 如果是客户库存那么item name定义为客户名+产品名   type为客户名
     */

    public ApiResponse<Object> getNewBarCode(String oldBarCode) {
        // 参数校验
        if (oldBarCode == null || oldBarCode.trim().isEmpty()) {
            throw new BusinessException("请输入有效的旧条码");
        }

        // 查询新条码
        String newBarCode = amazonProductRepository.getNewBarCode(oldBarCode);

        // 判断新条码是否存在
        if (newBarCode == null || newBarCode.trim().isEmpty()) {
            logger.warn("旧条码 [{}] 不存在对应的新条码", oldBarCode);
            throw new BusinessException("老标签不存在");
        }

        // 返回成功响应
        return ApiResponse.success("新条码获取成功",newBarCode);
    }


    }

