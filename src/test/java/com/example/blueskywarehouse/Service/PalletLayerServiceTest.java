package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.OptimalStorageLocationRepository;
import com.example.blueskywarehouse.Dao.PalletLayerRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PalletLayerServiceTest {
    @Mock
    private PalletLayerRepository palletLayerRepository;
    @InjectMocks
    private PalletLayerService palletLayerService;
    private final int itemId = 1;
    private final String oldBinCode = "OLD_BIN";
    private final String newBinCode = "NEW_BIN";

    // 库存充足：部分转移
    @Test
    void testUpdatePalett_PartialMove() {
        int currentOldStock = 10;
        int unitCount = 5;
        int currentNewStock = 5;

        when(palletLayerRepository.getStock(itemId, oldBinCode)).thenReturn(currentOldStock);
        when(palletLayerRepository.getStock(itemId, newBinCode)).thenReturn(currentNewStock);

        ApiResponse<?> response = palletLayerService.updatePalett(oldBinCode, newBinCode, itemId, unitCount);

        verify(palletLayerRepository).minusStock(itemId, unitCount, oldBinCode, currentOldStock);
        verify(palletLayerRepository).addStock(currentNewStock, itemId, unitCount, newBinCode);

        assertEquals("托盘位置移动成功", response.getMessage());
    }

    // 库存刚好：全量转移
    @Test
    void testUpdatePalett_ExactStockMove() {
        int currentOldStock = 5;
        int unitCount = 5;

        when(palletLayerRepository.getStock(itemId, oldBinCode)).thenReturn(currentOldStock);
        when(palletLayerRepository.getStock(itemId, newBinCode)).thenReturn(null);  // 新位置无库存

        ApiResponse<?> response =  palletLayerService.updatePalett(oldBinCode, newBinCode, itemId, unitCount);

        verify(palletLayerRepository).changePalettStatusInto49(itemId, oldBinCode);
        verify(palletLayerRepository).deleteItemId49();
        verify(palletLayerRepository).insertStockPalett(itemId, unitCount, newBinCode);
        verify(palletLayerRepository).insertStockBin(newBinCode);

        assertEquals("托盘位置移动成功", response.getMessage());
    }

    // 库存不足：抛出异常
    @Test
    void testUpdatePalett_InsufficientStock() {
        int currentOldStock = 2;
        int unitCount = 5;

        when(palletLayerRepository.getStock(itemId, oldBinCode)).thenReturn(currentOldStock);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                palletLayerService.updatePalett(oldBinCode, newBinCode, itemId, unitCount));

        assertEquals("库存不足", exception.getMessage());

        // 验证没有执行后续行为
        verify(palletLayerRepository, never()).minusStock(anyInt(), anyInt(), anyString(), anyInt());
        verify(palletLayerRepository, never()).addStock(anyInt(), anyInt(), anyInt(), anyString());
    }
}
