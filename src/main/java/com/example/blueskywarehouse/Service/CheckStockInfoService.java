package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dto.AllStockDto;
import com.example.blueskywarehouse.Dto.AllStockShadow;
import com.example.blueskywarehouse.Dto.AllStockLocationDto;
import com.example.blueskywarehouse.Dto.BinCodeFromItemDto;
import com.example.blueskywarehouse.Repository.CheckStockInfoRepository;
import com.example.blueskywarehouse.Repository.PalletInfoRepository;
import com.example.blueskywarehouse.Entity.PalletInfo;
import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Util.AllStockWhitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        List<AllStockShadow> allStockList = checkStockInfoRepository.getAllStock(AllStockWhitelist.ALLOWED);
        List<AllStockDto>allStockDtos=allStockList.stream().map(s-> new AllStockDto(
                s.getId(),
                s.getName(),
                s.getTotalBoxStock(),
                s.getTotalUnitStock()))
                .toList();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ApiResponse.success("Aktueller Bestand wie folgt", allStockDtos);
    }

    /**
     * Positionen und Lagerbestände für jedes Produkt abrufen
     */

    public ApiResponse<?> getAllStockLocation() {
        List<AllStockLocationDto> allStockLocation = checkStockInfoRepository.getAllStockLocation();
        return ApiResponse.success("Lagerbestände mit zugehörigen Positionen wie folgt", allStockLocation);

    }

    /**
     * Alle Lagerplätze für ein Produkt anhand der Artikel-ID abfragen
     */
    @Cacheable(value = "getLocationByItemId", key = "#itemId")
    public ApiResponse<?> getLocationByItemId(int itemId) {
        List<BinCodeFromItemDto> allBinCodeByItemId = checkStockInfoRepository.getAllBincodeByItemId(itemId);
        return ApiResponse.success("Lagerplätze für das nrodukt wie folgt", allBinCodeByItemId);
    }

    /**
     * Paletteninformationen anhand der Paletten-ID aktualisieren
     * @param id Paletten-ID
     * @param itemId Artikelnummern
     * @param boxStock Gesamtanzahl der Kartons
     * @param unitStock Gesamtstückzahl
     */
    @Caching(evict = {
            @CacheEvict(value = "allStock", allEntries = true),
            @CacheEvict(value = "getLocationByItemId", key = "#itemId")
    })
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
    @Caching(evict = {
            @CacheEvict(value = "allStock", allEntries = true),
            @CacheEvict(value = "getLocationByItemId", key = "#itemId")
    })
    @Transactional
    public ApiResponse<?> deletePalletinfoById(int id) {
        checkStockInfoRepository.deleteById((long) id);
        return ApiResponse.success("Palette erfolgreich gelöscht",null);
    }
}
