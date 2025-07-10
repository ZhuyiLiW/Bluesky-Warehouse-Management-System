package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.CheckStockInfoRepository;
import com.example.blueskywarehouse.Dao.ItemManagementRepository;
import com.example.blueskywarehouse.Entity.Item;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemManagementServiceTest {
    @Mock
    private ItemManagementRepository itemManagementRepository;
    @InjectMocks
    private ItemManagementService itemManagementService;

    @Test
    void addItemTest() {
        doNothing().when(itemManagementRepository).addItem("name1", "testType", 20, "testGroup");
        ApiResponse<?> response = itemManagementService.addItem("name1", "testType", 20, "testGroup");
        assertEquals("入新品类产品成功", response.getMessage());
    }

    @Test
    void updateItemTest() {
        // 构造一个Item对象，作为模拟findById的返回值
        Item item = new Item();
        item.setId(1);
        item.setName("oldName");
        item.setUnitPerBox(10);
        item.setProductGroup("oldGroup");

        // 模拟findById返回Optional.of(item)
        when(itemManagementRepository.findById(1L)).thenReturn(Optional.of(item));
        // 模拟save方法直接返回传入的对象
        when(itemManagementRepository.save(Mockito.<Item>any())).thenAnswer(invocation -> invocation.getArgument(0));

        // 调用测试方法
        ApiResponse<?> response = itemManagementService.updateItem(1, "name1", "testType", 20, "testGroup");

        // 断言返回结果
        assertEquals("更新新品类产品成功", response.getMessage());

        // 还可以验证Item的字段是否被正确更新
        assertEquals("name1", item.getName());
        assertEquals(20, item.getUnitPerBox());
        assertEquals("testGroup", item.getProductGroup());

        // 验证findById和save都被调用了
        verify(itemManagementRepository).findById(1L);
        verify(itemManagementRepository).save(item);
    }


    @Test
    void searchItemTest() {
        List<Item> mockData = Arrays.asList(new Item(1, "name1", "type1", 10, "group1"),
                new Item(2, "name2", "type2", 10, "group2"),
                new Item(3, "name3", "type3", 10, "group3")
        );
        when(itemManagementRepository.searchItem("name")).thenReturn(mockData);
        ApiResponse<?> response = itemManagementService.searchItem("name");
        List<Item> result = (List<Item>) response.getData();
        assertEquals("产品详细信息获取成功", response.getMessage());
        assertEquals(3, result.size());
        assertEquals(result.get(1).getType(), "type2");

    }

    @Test
    void searchItemLocationTest() {
        List<String> mockData = Arrays.asList("location1", "location2", "location3");
        when(itemManagementRepository.searchItemLocation(1)).thenReturn(mockData);
        ApiResponse<?> response = itemManagementService.searchItemLocation(1);
        List<String> result = (List<String>) response.getData();
        assertEquals("已成功获取产品位置", response.getMessage());
        assertEquals(3, result.size());
        assertEquals("location2", result.get(1));
    }

    @Test
    void searchItemCountTest() {
        Object[] mockData = new Object[]{1, 2};
        when(itemManagementRepository.searchItemCount(1)).thenReturn(mockData);
        ApiResponse<?> response = itemManagementService.searchItemCount(1);
        Object[]result= (Object[]) response.getData();
        assertEquals("已成功获取产品数量", response.getMessage());
        assertEquals(2,result[1]);

    }
}