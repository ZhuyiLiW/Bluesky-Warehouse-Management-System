package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Entity.PalletInfo;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Exception.VersionConflictException;
import com.example.blueskywarehouse.Repository.PalletInfoRepository;
import com.example.blueskywarehouse.Repository.PalletLayerRepository;
import com.example.blueskywarehouse.Repository.StorageSlotRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
//nur für Methode  updatePalett
@RunWith(MockitoJUnitRunner.class)
public class PalletLayerTest {
    @Mock
    PalletLayerRepository palletLayerRepository;
    @Mock
    PalletInfoRepository palletInfoRepository;
    @Mock
    StorageSlotRepository storageSlotRepository;
    @InjectMocks
    PalletLayerService palletLayerService;

    // Testfall: Wenn Original-Bestand größer ist → Bestand wird reduziert
    @Test
    public void whenOrigialStockBiggerSuccess_thenMinusOriginalStock() {
        // Arrange
        when(palletLayerRepository.getStock(anyInt(), anyString())).thenReturn(10).thenReturn(5);
        when(palletLayerRepository.getVersion(anyInt(), anyString())).thenReturn(3);
        when(palletLayerRepository.minusStock(anyInt(), anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(1);
        when(palletLayerRepository.addStock(anyInt(), anyInt(), anyInt(), anyString(), anyInt()))
                .thenReturn(1);

        // Act
        assertDoesNotThrow(() -> palletLayerService.updatePalett("oldBin","newBin",2,3));

        // Assert
        verify(palletLayerRepository, times(1))
                .minusStock(anyInt(), eq(3), anyString(), anyInt(), anyInt());
        verify(palletLayerRepository, atLeastOnce()).getStock(anyInt(), anyString());
        verify(palletLayerRepository, atLeastOnce()).getVersion(anyInt(), anyString());
        verify(palletLayerRepository, times(1))
                .addStock(anyInt(), anyInt(), eq(3), anyString(), anyInt());
        verify(palletLayerRepository, never())
                .changePalettStatusInto49(anyInt(), anyString(), anyInt());
        verify(palletInfoRepository, never()).deleteItemId49();
    }

    // Testfall: Wenn Original-Bestand gleich ist → Palettenstatus wird geändert
    @Test
    public void whenOrigialStockSameSuccess_thenChangePalettStatus() {
        // Arrange
        when(palletLayerRepository.getStock(anyInt(), anyString())).thenReturn(10).thenReturn(5);
        when(palletLayerRepository.getVersion(anyInt(), anyString())).thenReturn(3);
        when(palletLayerRepository.changePalettStatusInto49(anyInt(), anyString(), eq(3)))
                .thenReturn(1);
        when(palletLayerRepository.addStock(anyInt(), anyInt(), anyInt(), anyString(), anyInt()))
                .thenReturn(1);

        // Act
        assertDoesNotThrow(() -> palletLayerService.updatePalett("oldBin","newBin",2,10));

        // Assert
        verify(palletLayerRepository, never())
                .minusStock(anyInt(), anyInt(), anyString(), anyInt(), anyInt());
        verify(palletLayerRepository, atLeastOnce()).getStock(anyInt(), anyString());
        verify(palletLayerRepository, atLeastOnce()).getVersion(anyInt(), anyString());
        verify(palletLayerRepository, times(1))
                .changePalettStatusInto49(anyInt(), anyString(), eq(3));
        verify(palletInfoRepository, times(1)).deleteItemId49();
        verify(palletLayerRepository, times(1))
                .addStock(anyInt(), anyInt(), eq(10), anyString(), anyInt());
    }

    // Testfall: Wenn neuer Bestand null → Palettenstatus ändern, neue Paletteninfo speichern und neuen Bin einfügen
    @Test
    public void whenOrigialStockSameSuccessNewStockIsNull_thenChangePalettStatus() {
        // Arrange
        when(palletLayerRepository.getStock(anyInt(), eq("oldBin"))).thenReturn(10);
        when(palletLayerRepository.getStock(anyInt(), eq("newBin"))).thenReturn(0);
        when(palletLayerRepository.getVersion(anyInt(), eq("oldBin"))).thenReturn(3);
        when(palletLayerRepository.getVersion(anyInt(), eq("newBin"))).thenReturn(1);
        when(palletLayerRepository.changePalettStatusInto49(anyInt(), anyString(), eq(3)))
                .thenReturn(1);

        // Act
        assertDoesNotThrow(() -> palletLayerService.updatePalett("oldBin","newBin",2,10));

        // Assert
        verify(palletLayerRepository, never())
                .minusStock(anyInt(), anyInt(), anyString(), anyInt(), anyInt());

        verify(palletLayerRepository, atLeastOnce()).getStock(anyInt(), anyString());
        verify(palletLayerRepository, atLeastOnce()).getVersion(anyInt(), anyString());

        verify(palletLayerRepository, times(1))
                .changePalettStatusInto49(anyInt(), anyString(), eq(3));
        verify(palletInfoRepository, times(1)).deleteItemId49();

        verify(palletLayerRepository, never())
                .addStock(anyInt(), anyInt(), anyInt(), anyString(), anyInt());

        verify(palletInfoRepository, times(1)).save(any(PalletInfo.class));
        verify(storageSlotRepository, times(1)).insertStockBin(eq("newBin"));
    }

    // Testfall: Wenn Bestand kleiner als angefordert → BusinessException wird geworfen
    @Test(expected = BusinessException.class)
    public void whenStockIsLess_ThenBusinessException() {
        // Arrange
        when(palletLayerRepository.getStock(anyInt(), eq("oldBin"))).thenReturn(2);
        when(palletLayerRepository.getVersion(anyInt(), eq("oldBin"))).thenReturn(1);

        // Act
        palletLayerService.updatePalett("oldBin", "newBin", 2, 5);

        // Assert
        verify(palletLayerRepository, never()).minusStock(anyInt(), anyInt(), anyString(), anyInt(), anyInt());
        verify(palletLayerRepository, never()).addStock(anyInt(), anyInt(), anyInt(), anyString(), anyInt());
        verify(palletLayerRepository, never()).changePalettStatusInto49(anyInt(), anyString(), anyInt());
    }

    // Testfall: Wenn Repository-Update fehlschlägt (Versionskonflikt) → VersionConflictException wird geworfen
    @Test(expected = VersionConflictException.class)
    public void whenMinusStockError_thenVersionConflictException() {
        // Arrange
        when(palletLayerRepository.getStock(anyInt(), eq("oldBin"))).thenReturn(10);
        when(palletLayerRepository.getVersion(anyInt(), eq("oldBin"))).thenReturn(3);
        when(palletLayerRepository.minusStock(anyInt(), anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(0);

        // Act
        palletLayerService.updatePalett("oldBin", "newBin", 2, 5);
    }
}

