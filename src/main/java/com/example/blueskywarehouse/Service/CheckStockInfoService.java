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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
     * Alle Bestandsinformationen abrufen
     */

    @Cacheable(value = "allStock")

    public ApiResponse<?> getAllStockInfo() {
        List<AllStock> allStockList = checkStockInfoRepository.getAllStock();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Aktueller authentifizierter Benutzer: " + authentication.getName());
        System.out.println("Berechtigungen: " + authentication.getAuthorities());
        System.out.println("查询数据库...");
        return ApiResponse.success("Aktueller Bestand wie folgt", allStockList);
    }

    /**
     * Positionen und Lagerbestände für jedes Produkt abrufen
     */

    public ApiResponse<?> getAllStockLocation() {
        // Hier Object[] verwenden, da location gruppiert ist. Direkte Abbildung auf AllStock ist problematisch, daher manuelle Abbildung notwendig
        List<Object[]> allStockLocation = checkStockInfoRepository.getAllStockLocation();
        List<StockWithLocation> transferAllStockLocation = new ArrayList<>();
        // Manuelle Abbildung aller Produkt-Lagerplatzinformationen
        for (Object[] row : allStockLocation) {
            String name = (String) row[0];
            String location = (String) row[1];

            // Vermeidung von Typumwandlungsfehlern
            double totalBoxStock =row[2]==null?0: ((Number) row[2]).doubleValue();
            double totalUnitStock =row[3]==null?0:  ((Number) row[3]).doubleValue();

            StockWithLocation allStock = new StockWithLocation(name, location, totalBoxStock, totalUnitStock);
            transferAllStockLocation.add(allStock);
        }

        return ApiResponse.success("Lagerbestände mit zugehörigen Positionen wie folgt", transferAllStockLocation);

    }

    /**
     * Alle Lagerplätze für ein Produkt anhand der Artikel-ID abfragen
     */

    public ApiResponse<?> getLocationByItemId(int itemId) {
        List<String> allBinCodeByItemId = checkStockInfoRepository.getAllBincodeByItemId(itemId);
        return ApiResponse.success("Lagerplätze für das Produkt wie folgt", allBinCodeByItemId);
    }

    /**
     * Paletteninformationen anhand der Paletten-ID aktualisieren
     * @param id Paletten-ID
     * @param itemId Artikelnummer
     * @param boxStock Gesamtanzahl der Kartons
     * @param unitStock Gesamtstückzahl
     */
    @CacheEvict(value = "allStock", allEntries = true)
    @Transactional
    public ApiResponse<?> updatePalletinfoById(int id, int itemId, double boxStock, int unitStock) {
        PalletInfo palletInfo = palletInfoRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Paletteninformation nicht gefunden, id=" + id));
        palletInfo.setItemId(itemId);
        palletInfo.setBoxStock(boxStock);
        palletInfo.setUnitStock(unitStock);
        palletInfoRepository.save(palletInfo);  // save() direkt aufrufen, JPA führt update durch
        logger.info("Paletteninformation erfolgreich aktualisiert, id={}, itemId={}, boxStock={}, unitStock={}", id, itemId, boxStock, unitStock);
        return ApiResponse.success("Paletteninformation erfolgreich aktualisiert", null);
    }
    @CacheEvict(value = "allStock", allEntries = true)
    @Transactional
    public ApiResponse<?> deletePalletinfoById(int id) {
        checkStockInfoRepository.deletePalletinfoById(id);
        return ApiResponse.success("Palette erfolgreich gelöscht",null);
    }
}
