package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.OptimalStorageLocationRepository;
import com.example.blueskywarehouse.Dao.PriceListRepository;
import com.example.blueskywarehouse.Entity.PriceList;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Exception.InvalidParameterException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class PriceListService {
    @Autowired
    private PriceListRepository priceListRepository;
    Logger logger = LoggerFactory.getLogger(OptimalStorageLocationService.class);

    /**
     * 向价格表和价格历史表中插入一条新的价格记录。
     * <p>
     * 此方法执行两个操作：
     * 1. 将当前价格插入 price_list 表（用于当前价格查询）
     * 2. 同时将价格插入 price_history 表（用于生成价格变化趋势图）
     *
     * @param itemId 商品的唯一标识 ID，必须大于 0
     * @param price  商品当前价格，必须为非负数
     */
    @Transactional
    public ApiResponse<?> insertPriceList(int itemId, double price, String remark) {
        //itemId是否已经存在
        List<Integer>isItemIdExisted=priceListRepository.checkItemId(itemId);
        if(isItemIdExisted.size()>0){
            logger.warn("item id 已经存在 itemId={}, price={}", itemId, price);
            throw new BusinessException("物料价格已经存在");
        }
        // 参数校验（可选）
        if (itemId <= 0 || price < 0) {
            logger.warn("插入价格失败：参数不合法 itemId={}, price={}", itemId, price);
            throw new InvalidParameterException("参数不合法");
        }

        LocalDate date = LocalDate.now();
            // 插入当前价格
            priceListRepository.insertPriceList(itemId, price,remark);

            // 记录价格历史（用于生成价格趋势图）
            priceListRepository.insertPriceListHistory(itemId, price, date);

            logger.info("成功插入价格和历史记录，itemId={}, price={}, date={}", itemId, price, date);
            return ApiResponse.success("价格插入成功",null);
        }
    /**
     * 更新指定商品的价格，并记录价格变动历史。
     * <p>
     * 此方法执行两个数据库操作：
     * 1. 更新 price_list 表中对应 itemId 的当前价格；
     * 2. 向 price_history 表插入一条记录，记录此次变更（用于生成价格趋势图）。
     *
     * @param itemId 商品的唯一标识 ID，必须大于 0
     * @param price  新的商品价格，必须为非负数
     */
    @Transactional
    public ApiResponse<?> updatePriceList(int itemId, double price) {
        LocalDate date = LocalDate.now();

            // 参数校验（可选）
            if (itemId <= 0 || price < 0) {
                logger.warn("更新价格失败：参数不合法 itemId={}, price={}", itemId, price);
                throw new InvalidParameterException("参数不合法");
            }

            // 更新当前价格
            priceListRepository.updatePriceList(itemId, price);

            // 记录历史价格（便于生成价格折线图）
            priceListRepository.insertPriceListHistory(itemId, price, date);

            logger.info("成功更新商品价格 itemId={}, newPrice={}, date={}", itemId, price, date);
            return ApiResponse.success("价格更新成功",null);
    }


    /**
     * 查询并返回所有产品的价格列表。
     * <p>
     * 该方法从 price_list 表中查询所有记录，并返回给前端用于展示。
     */

    public ApiResponse<?> showPriceList() {

            List<PriceList> priceList = priceListRepository.searchPriceList();

            if (priceList == null || priceList.isEmpty()) {
                logger.warn("查询价格列表：无数据");
                throw new BusinessException("未查到该商品历史价格");
            }

            logger.info("成功查询所有产品价格，条目数: {}", priceList.size());
            return ApiResponse.success("所有产品价格如下:", priceList);
    }

    /**
     * 查询指定商品的历史价格记录。
     * <p>
     * 用于生成该商品的价格变动折线图，数据来源于 price_history 表。
     *
     * @param itemId 商品唯一标识 ID，必须大于 0
     */

    public ApiResponse<?> showPriceListHistory(int itemId) {

            List<PriceList> priceHistoryList = priceListRepository.searchPriceHistory(itemId);

            if (priceHistoryList == null || priceHistoryList.isEmpty()) {
                logger.warn("未查询到商品 ID={} 的价格历史", itemId);
                throw new BusinessException("未查到该商品历史价格");
            }

            logger.info("成功查询商品 ID={} 的价格历史，共 {} 条记录", itemId, priceHistoryList.size());
            return ApiResponse.success("成功获取相关产品价格变动折线图", priceHistoryList);
    }


}