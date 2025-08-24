package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Repository.OptimalStorageLocationRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Util.SqlLikeEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OptimalStorageLocationService {

    @Autowired
    private OptimalStorageLocationRepository optimalStorageLocationRepository;

    Logger logger = LoggerFactory.getLogger(OptimalStorageLocationService.class);

    /**
     * Gibt die Liste der optimalen Lagerplätze (bin_code) für ein bestimmtes Produkt (itemId) zurück.
     * <p>
     * Diese Methode sucht zunächst nach dem slot_code mit der höchsten Lagerbestandsmenge für das Produkt,
     * und sucht dann in ungenutzten bins nach empfohlenen Lagerplätzen, die zum slot_code Muster passen.
     *
     * @param itemId Die Produkt-ID
     * @return Eine Liste empfohlener bin_codes, oder 404 Fehler, falls kein geeigneter Lagerplatz gefunden wurde
     */
    public ApiResponse<?> getOptimalBin(int itemId) {
        logger.info("Starte Abruf der optimalen Lagerplätze für itemId={}", itemId);

        // Optimalen Slot nach Lagerbestand abrufen
        String optimalSlot = optimalStorageLocationRepository.getOptimalSlot(itemId);
        logger.debug("Ermittelter optimaler slot_code: {}", optimalSlot);

        // Verfügbare bins basierend auf slot_code abfragen
        SqlLikeEscaper.escape(optimalSlot);
        List<String> binList = optimalStorageLocationRepository.getOptimalBinlist(optimalSlot);
        if (binList.isEmpty()) {
            logger.warn("Kein geeigneter Lagerplatz für itemId={} gefunden", itemId);
            throw new BusinessException("Systemfehler: Kein geeigneter Lagerplatz gefunden");
        }

        logger.info("{} verfügbare Lagerplätze erfolgreich abgerufen", binList.size());
        return ApiResponse.success("Erfolgreich optimale Lagerplätze abgerufen", binList);
    }

    /**
     * Gibt alle freien Lagerplätze (bins) zurück, die aktuell nicht von Paletten belegt sind.
     */
    public ApiResponse<?> getAllEmptyBin(int itemId){
        String optimalSlot = optimalStorageLocationRepository.getOneOptimalSlot(itemId);
        SqlLikeEscaper.escape(optimalSlot);
        List<String> allEmptyBinList = optimalStorageLocationRepository.getAllEmptyBinListBySlot(optimalSlot);

        if (allEmptyBinList == null || allEmptyBinList.isEmpty()) {
            logger.warn("Keine freien Lagerplätze gefunden");
            throw new BusinessException("Systemfehler: Kein geeigneter Lagerplatz gefunden");
        }
        logger.info("Erfolgreich alle freien Lagerplätze abgerufen: {}", allEmptyBinList.size());
        return ApiResponse.success("Alle verfügbaren Lagerplätze erfolgreich abgerufen", allEmptyBinList);
    }

    @Transactional
    public ApiResponse<?> InsertPalletsIntoNewBin(String newBinCode,String oldBinCode){
        String newSlotCode = newBinCode.split("-")[0];
        optimalStorageLocationRepository.InsertPalletsIntoNewBin(newBinCode, newSlotCode, oldBinCode);
        logger.info("Lagerplatz erfolgreich verschoben");
        return ApiResponse.success("Lagerplatz erfolgreich verschoben", null);
    }

}
