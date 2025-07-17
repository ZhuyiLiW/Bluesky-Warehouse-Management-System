package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.OptimalStorageLocationRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OptimalStorageLocationService {

    @Autowired
    private OptimalStorageLocationRepository optimalStorageLocationRepository;

    Logger logger = LoggerFactory.getLogger(OptimalStorageLocationService.class);

    /**
     * 获取指定商品（itemId）在库存中最优的仓号列表。
     * <p>
     * 该方法首先通过库存信息查找商品所在库存数量最多的 slot_code，
     * 然后在未被占用的 bin 中查找与该 slot_code 模式匹配的推荐入库位置。
     *
     * @param itemId 商品的 ID
     * @return 返回包含推荐 bin_code 的列表，若未找到合适仓位则返回 404 错误
     */

    public ApiResponse<?> getOptimalBin(int itemId) {
            logger.info("开始获取 itemId={} 的最佳仓位", itemId);

            // 获取最优 slot（按库存量排序）
            String optimalSlot = optimalStorageLocationRepository.getOptimalSlot(itemId);
            logger.debug("获取到的最优slot_code为: {}", optimalSlot);

            // 基于 slot_code 获取可用 bin 列表
            List<String> binList = optimalStorageLocationRepository.getOptimalBinlist(optimalSlot);
            if (binList.isEmpty()) {
                logger.warn("未找到 itemId={} 的合适仓位", itemId);
                throw new BusinessException("系统错误，未找到合适仓位");
            }

            logger.info("成功获取到 {} 个可用仓位", binList.size());
            return ApiResponse.success("成功获取最佳仓位集合", binList);
    }

    /**
     *
     *获取所有空闲的仓位（bin），即当前未被 pallet 使用的仓位。
     *
     */

    public ApiResponse<?> getAllEmptyBin(int itemId){

           String optimalSlot = optimalStorageLocationRepository.getOneOptimalSlot(itemId);
           List<String>allEmptyBinList=optimalStorageLocationRepository.getAllEmptyBinListBySlot(optimalSlot);

            if (allEmptyBinList == null || allEmptyBinList.isEmpty()) {
                logger.warn("未找到任何空的仓位信息");
                throw new BusinessException("系统错误，未找到合适仓位");
            }
            logger.info("成功获取所有空的仓位数量：{}", allEmptyBinList);
            return ApiResponse.success("成功获取所有可用仓位", allEmptyBinList);

    }
    @Transactional
    public ApiResponse<?> InsertPalletsIntoNewBin(String newBinCode,String oldBinCode){
        String newSlotCode=newBinCode.split("-")[0];
        optimalStorageLocationRepository.InsertPalletsIntoNewBin(newBinCode,newSlotCode,oldBinCode);
        logger.info("仓位移动成功");
        return ApiResponse.success("仓位移动成功",null);
    }

}

