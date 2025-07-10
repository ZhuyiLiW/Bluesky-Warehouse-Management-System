package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.ItemManagementRepository;
import com.example.blueskywarehouse.Entity.Item;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 产品管理服务类，负责处理商品相关的业务逻辑，如添加、更新、查询产品信息。
 */
@Service
public class ItemManagementService {

    @Autowired
    private ItemManagementRepository itemManagementRepository;

    Logger logger = LoggerFactory.getLogger(ItemManagementService.class);

    /**
     * 添加一个新的产品信息。 如果是客户库存那么item name定义为客户名+产品名   type为客户名
     */
    @Transactional
    public ApiResponse<?> addItem(String name, String type, Integer unitPerBox, String productGroup) {
            itemManagementRepository.addItem(name, type, unitPerBox, productGroup);
            return ApiResponse.success("入新品类产品成功",null);
    }

    /**
     * 更新已有产品的名称和类型。
     */
    @Transactional
    public ApiResponse<?> updateItem(int id, String name, String type, int unitPerBox,String productGroup) {
        Item item=itemManagementRepository.findById((long) id).orElseThrow(() -> new RuntimeException("托盘信息不存在，id=" + id));
        item.setName(name);
        item.setUnitPerBox(unitPerBox);
        item.setProductGroup(productGroup);
        itemManagementRepository.save(item);
        return ApiResponse.success("更新新品类产品成功",null);
    }

    /**
     * 根据产品名称模糊搜索产品信息。
     */

    public ApiResponse<?> searchItem(String name) {
        name = name == null ? "" : name.trim();
            List<Item> itemsList = Optional.ofNullable(itemManagementRepository.searchItem(name))
                    .orElse(Collections.emptyList());
            if(itemsList.size()==0)throw new BusinessException("物料不存在");
            return ApiResponse.success("产品详细信息获取成功", itemsList);
    }

    /**
     * 查询某个品类产品的位置
     */

    public ApiResponse<?> searchItemLocation(int itemId) {
            List<String> itemsLocationList = itemManagementRepository.searchItemLocation(itemId);
            return ApiResponse.success("已成功获取产品位置", itemsLocationList);
    }
    /**
     * 查询某个品类产品的数量 itemsCount[0]是箱数 itemsCount[1]是件数
     */

    public ApiResponse<?> searchItemCount(int itemId) {
           Object[] itemCount =itemManagementRepository.searchItemCount(itemId);
            return ApiResponse.success("已成功获取产品数量", itemCount);

    }

}
