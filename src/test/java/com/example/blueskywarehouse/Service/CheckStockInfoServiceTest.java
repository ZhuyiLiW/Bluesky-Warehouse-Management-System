package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.CheckStockInfoRepository;
import com.example.blueskywarehouse.Dao.PalletInfoRepository;
import com.example.blueskywarehouse.Entity.AllStock;
import com.example.blueskywarehouse.Entity.PalletInfo;
import com.example.blueskywarehouse.Entity.StockWithLocation;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CheckStockInfoServiceTest {
    @Mock
    private CheckStockInfoRepository checkStockInfoRepository;
    @InjectMocks
    private CheckStockInfoService checkStockInfoService;
    @InjectMocks
    PalletInfoRepository palletInfoRepository;
    @Test
    void getAllStockLocation_successTest() {
        // 准备模拟数据
        List<Object[]> mockData = Arrays.asList(
                new Object[]{"TestA", "仓库1", 10.0, 100.0},
                new Object[]{"TestB", "仓库2", null, 50.0},
                new Object[]{"TestC", "仓库3", 5.0, null}
        );

        when(checkStockInfoRepository.getAllStockLocation()).thenReturn(mockData);

        // 调用 service 方法
        ApiResponse<?> response = checkStockInfoService.getAllStockLocation();

        // 断言 response 正确
        assertEquals("现存库存对应位置如下", response.getMessage());

        List<StockWithLocation> result = (List<StockWithLocation>) response.getData();
        assertEquals(3, result.size());

        StockWithLocation item1 = result.get(0);
        assertEquals("TestA", item1.getName());
        assertEquals("仓库1", item1.getLocation());
        assertEquals(10.0, item1.getTotalBoxStock());
        assertEquals(100.0, item1.getTotalUnitStock());

        StockWithLocation item2 = result.get(1);
        assertEquals(0.0, item2.getTotalBoxStock()); // null 被转成 0.0
        assertEquals(50.0, item2.getTotalUnitStock());

        StockWithLocation item3 = result.get(2);
        assertEquals(5.0, item3.getTotalBoxStock());
        assertEquals(0.0, item3.getTotalUnitStock()); // null 被转成 0.0
    }
    @Test
    @Disabled
    void testGetAllStockTest(){
        List<AllStock> mockData = Arrays.asList(
                new AllStock(1, "仓库1", 10.0, 100.0),
                new AllStock(2, "仓库2", 0, 100.0),
                new AllStock(3, "仓库3", 10.0, 100.0)
        );
        when(checkStockInfoRepository.getAllStock()).thenReturn(mockData);
        ApiResponse<?> response = checkStockInfoService.getAllStockInfo();
        List<AllStock> result= (List<AllStock>) response.getData();
        assertEquals("现存库存如下",response.getMessage());
        assertEquals(3,result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(10, result.get(0).getTotalBoxStock());
        assertEquals(100, result.get(0).getTotalUnitStock());
    }
    @Test
    void getAllStockByIdTest(){
        int itemId=4;
        List<String>mockData =Arrays.asList(
           "test-location-01","test-location-02","test-location-03"
        );
        when(checkStockInfoRepository.getAllBincodeByItemId(itemId)).thenReturn(mockData);
        ApiResponse<?> response = checkStockInfoService.getLocationByItemId(4);
        List<String>result= (List<String>) response.getData();
        assertEquals("该商品的库存位置如下",response.getMessage());
        assertEquals(3,result.size());
        assertEquals("test-location-01", result.get(0));
    }
    @Test
    void updatePalletinfoByIdTest() {
        int id = 1;
        int itemId = 101;
        double boxStock = 3;
        int unitStock = 33;

        // 创建一个已有的实体，用来模拟findById返回
        PalletInfo existingPallet = new PalletInfo();
        existingPallet.setId(id);
        existingPallet.setItemId(100);  // 旧值
        existingPallet.setBoxStock(1.0);
        existingPallet.setUnitStock(10);

        // 模拟 findById 返回 Optional.of(existingPallet)
        when(palletInfoRepository.findById((long) id)).thenReturn(Optional.of(existingPallet));

        // 模拟 save 方法，返回传入的对象
        when(palletInfoRepository.save(any(PalletInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 调用service方法
        ApiResponse<?> response = checkStockInfoService.updatePalletinfoById(id, itemId, boxStock, unitStock);

        // 验证save方法调用过一次，且参数id正确
        verify(palletInfoRepository, times(1)).save(existingPallet);

        // 断言更新后的实体属性正确
        assertEquals(itemId, existingPallet.getItemId());
        assertEquals(boxStock, existingPallet.getBoxStock());
        assertEquals(unitStock, existingPallet.getUnitStock());

        // 断言返回成功
        assertEquals("托盘信息更新成功",response.getMessage());
    }
    @Test
    void  deletePalletinfoByIdTest(){
        int id=1;
        doNothing().when(checkStockInfoRepository).deletePalletinfoById(id);
        ApiResponse<?> response = checkStockInfoService.deletePalletinfoById(id);
        assertEquals("删除托盘成功",response.getMessage());
        // 验证是否真的被调用过一次
        verify(checkStockInfoRepository, times(1)).deletePalletinfoById(id);

    }
}
