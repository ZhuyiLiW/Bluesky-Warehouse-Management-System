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
        // Vorbereitung von Mock-Daten
        List<Object[]> mockData = Arrays.asList(
                new Object[]{"TestA", "Lager 1", 10.0, 100.0},
                new Object[]{"TestB", "Lager 2", null, 50.0},
                new Object[]{"TestC", "Lager 3", 5.0, null}
        );

        when(checkStockInfoRepository.getAllStockLocation()).thenReturn(mockData);

        // Aufruf der Service-Methode
        ApiResponse<?> response = checkStockInfoService.getAllStockLocation();

        // Überprüfen der Rückgabe
        assertEquals("Bestandsorte wie folgt", response.getMessage());

        List<StockWithLocation> result = (List<StockWithLocation>) response.getData();
        assertEquals(3, result.size());

        StockWithLocation item1 = result.get(0);
        assertEquals("TestA", item1.getName());
        assertEquals("Lager 1", item1.getLocation());
        assertEquals(10.0, item1.getTotalBoxStock());
        assertEquals(100.0, item1.getTotalUnitStock());

        StockWithLocation item2 = result.get(1);
        assertEquals(0.0, item2.getTotalBoxStock()); // null wurde zu 0.0 konvertiert
        assertEquals(50.0, item2.getTotalUnitStock());

        StockWithLocation item3 = result.get(2);
        assertEquals(5.0, item3.getTotalBoxStock());
        assertEquals(0.0, item3.getTotalUnitStock()); // null wurde zu 0.0 konvertiert
    }

    @Test
    @Disabled
    void testGetAllStockTest() {
        List<AllStock> mockData = Arrays.asList(
                new AllStock(1, "Lager 1", 10.0, 100.0),
                new AllStock(2, "Lager 2", 0, 100.0),
                new AllStock(3, "Lager 3", 10.0, 100.0)
        );

        when(checkStockInfoRepository.getAllStock()).thenReturn(mockData);
        ApiResponse<?> response = checkStockInfoService.getAllStockInfo();
        List<AllStock> result = (List<AllStock>) response.getData();

        assertEquals("Aktueller Bestand wie folgt", response.getMessage());
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(10, result.get(0).getTotalBoxStock());
        assertEquals(100, result.get(0).getTotalUnitStock());
    }

    @Test
    void getAllStockByIdTest() {
        int itemId = 4;
        List<String> mockData = Arrays.asList(
                "test-standort-01", "test-standort-02", "test-standort-03"
        );

        when(checkStockInfoRepository.getAllBincodeByItemId(itemId)).thenReturn(mockData);
        ApiResponse<?> response = checkStockInfoService.getLocationByItemId(4);
        List<String> result = (List<String>) response.getData();

        assertEquals("Lagerorte dieses Artikels wie folgt", response.getMessage());
        assertEquals(3, result.size());
        assertEquals("test-standort-01", result.get(0));
    }

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
        doNothing().when(checkStockInfoRepository).deletePalletinfoById(id);
        ApiResponse<?> response = checkStockInfoService.deletePalletinfoById(id);

        assertEquals("Palette erfolgreich gelöscht", response.getMessage());

        // Verifizieren, dass delete einmal aufgerufen wurde
        verify(checkStockInfoRepository, times(1)).deletePalletinfoById(id);
    }
}
