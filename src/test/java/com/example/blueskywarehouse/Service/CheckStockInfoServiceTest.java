package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dto.AllStockLocationDto;
import com.example.blueskywarehouse.Repository.CheckStockInfoRepository;
import com.example.blueskywarehouse.Repository.PalletInfoRepository;
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

@ExtendWith(MockitoExtension.class)
public class CheckStockInfoServiceTest {

    @Mock
    private CheckStockInfoRepository checkStockInfoRepository;

    @InjectMocks
    private CheckStockInfoService checkStockInfoService;

    @InjectMocks
    PalletInfoRepository palletInfoRepository;





    @Test
    void updatePalletinfoByIdTest() {
        int id = 1;
        int itemId = 101;
        double boxStock = 3;
        int unitStock = 33;

        // Vorhandenes PalletInfo-Objekt vorbereiten
        PalletInfo existingPallet = new PalletInfo();
        existingPallet.setId(id);
        existingPallet.setItemId(100);  // alter Wert
        existingPallet.setBoxStock(1.0);
        existingPallet.setUnitStock(10);

        // findById simulieren
        when(palletInfoRepository.findById((long) id)).thenReturn(Optional.of(existingPallet));

        // save simulieren, Rückgabe = übergebenes Objekt
        when(palletInfoRepository.save(any(PalletInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Service-Methode aufrufen
        ApiResponse<?> response = checkStockInfoService.updatePalletinfoById(id, itemId, boxStock, unitStock);

        // Verifizieren, dass save aufgerufen wurde
        verify(palletInfoRepository, times(1)).save(existingPallet);

        // Überprüfen, ob Werte korrekt aktualisiert wurden
        assertEquals(itemId, existingPallet.getItemId());
        assertEquals(boxStock, existingPallet.getBoxStock());
        assertEquals(unitStock, existingPallet.getUnitStock());

        // Rückgabe überprüfen
        assertEquals("Paletteninformationen erfolgreich aktualisiert", response.getMessage());
    }

    @Test
    void deletePalletinfoByIdTest() {
        int id = 1;
        doNothing().when(checkStockInfoRepository).deleteById((long) id);
        ApiResponse<?> response = checkStockInfoService.deletePalletinfoById(id);

        assertEquals("Palette erfolgreich gelöscht", response.getMessage());

        // Verifizieren, dass delete einmal aufgerufen wurde
        verify(checkStockInfoRepository, times(1)).deleteById((long) id);
    }
}
