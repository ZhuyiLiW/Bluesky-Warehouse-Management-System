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

    // Ausreichender Lagerbestand: Teilweise Verschiebung
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

        assertEquals("Palettenposition erfolgreich verschoben", response.getMessage());
    }

    // Genauer Lagerbestand: Vollständige Verschiebung
    @Test
    void testUpdatePalett_ExactStockMove() {
        int currentOldStock = 5;
        int unitCount = 5;

        when(palletLayerRepository.getStock(itemId, oldBinCode)).thenReturn(currentOldStock);
        when(palletLayerRepository.getStock(itemId, newBinCode)).thenReturn(null);  // Kein Lagerbestand an neuer Position

        ApiResponse<?> response =  palletLayerService.updatePalett(oldBinCode, newBinCode, itemId, unitCount);

        verify(palletLayerRepository).changePalettStatusInto49(itemId, oldBinCode);
        verify(palletLayerRepository).deleteItemId49();
        verify(palletLayerRepository).insertStockPalett(itemId, unitCount, newBinCode);
        verify(palletLayerRepository).insertStockBin(newBinCode);

        assertEquals("Palettenposition erfolgreich verschoben", response.getMessage());
    }

    // Unzureichender Lagerbestand: Ausnahme werfen
    @Test
    void testUpdatePalett_InsufficientStock() {
        int currentOldStock = 2;
        int unitCount = 5;

        when(palletLayerRepository.getStock(itemId, oldBinCode)).thenReturn(currentOldStock);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                palletLayerService.updatePalett(oldBinCode, newBinCode, itemId, unitCount));

        assertEquals("Unzureichender Lagerbestand", exception.getMessage());

        // Verifizieren, dass keine weiteren Aktionen ausgeführt wurden
        verify(palletLayerRepository, never()).minusStock(anyInt(), anyInt(), anyString(), anyInt());
        verify(palletLayerRepository, never()).addStock(anyInt(), anyInt(), anyInt(), anyString());
    }
}
