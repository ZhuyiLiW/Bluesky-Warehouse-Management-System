package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.CheckStockInfoRepository;
import com.example.blueskywarehouse.Dao.PalletInfoRepository;
import com.example.blueskywarehouse.Entity.AllStock;
import com.example.blueskywarehouse.Entity.PalletInfo;
import com.example.blueskywarehouse.Entity.StockWithLocation;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckStockInfoService {
    @Autowired
    CheckStockInfoRepository checkStockInfoRepository;
    @Autowired
    PalletInfoRepository palletInfoRepository;
    Logger logger = LoggerFactory.getLogger(CheckStockInfoService.class);
    /**
     * 获取全部库存信息
     */

    public ApiResponse<?> getAllStockInfo() {
            List<AllStock> allStockList = checkStockInfoRepository.getAllStock();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("当前认证用户：" + authentication.getName());
        System.out.println("权限：" + authentication.getAuthorities());
            return ApiResponse.success("现存库存如下", allStockList);
    }

    /**
     * 获取每个商品对应的位置和库存汇总
     */

    public ApiResponse<?> getAllStockLocation() {
            //这里Object[]因为 location是 Group 如果把对象直接变成AllStock会有无法映射的问题 所以需要手动映射
            List<Object[]> allStockLocation = checkStockInfoRepository.getAllStockLocation();
            List<StockWithLocation> transferAllStockLocation = new ArrayList<>();
           //手动映射所有商品对应仓位信息
            for (Object[] row : allStockLocation) {
                String name = (String) row[0];
                String location = (String) row[1];

                // 防止类型转换异常
                double totalBoxStock =row[2]==null?0: ((Number) row[2]).doubleValue();
                double totalUnitStock =row[3]==null?0:  ((Number) row[3]).doubleValue();

                StockWithLocation allStock = new StockWithLocation(name, location, totalBoxStock, totalUnitStock);
                transferAllStockLocation.add(allStock);
            }

            return ApiResponse.success("现存库存对应位置如下", transferAllStockLocation);

    }

    /**
     * 根据 itemId 查询该商品所在的所有货位
     */

    public ApiResponse<?> getLocationByItemId(int itemId) {
            List<String> allBinCodeByItemId = checkStockInfoRepository.getAllBincodeByItemId(itemId);
            return ApiResponse.success("该商品的库存位置如下", allBinCodeByItemId);
    }

    /**
     * 根据托盘ID更新托盘信息
     * @param id 托盘ID
     * @param itemId 物料编号
     * @param boxStock 总箱数
     * @param unitStock 总件数
     */
    @Transactional
    public ApiResponse<?> updatePalletinfoById(int id, int itemId, double boxStock, int unitStock) {
        PalletInfo palletInfo = palletInfoRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("托盘信息不存在，id=" + id));
        palletInfo.setItemId(itemId);
        palletInfo.setBoxStock(boxStock);
        palletInfo.setUnitStock(unitStock);
        palletInfoRepository.save(palletInfo);  // 直接调用save()，JPA会帮你update
        logger.info("托盘信息更新成功，id={}, itemId={}, boxStock={}, unitStock={}", id, itemId, boxStock, unitStock);
        return ApiResponse.success("托盘信息更新成功", null);
    }

    @Transactional
    public ApiResponse<?> deletePalletinfoById(int id) {
        checkStockInfoRepository.deletePalletinfoById(id);
        return ApiResponse.success("删除托盘成功",null);
    }
}
