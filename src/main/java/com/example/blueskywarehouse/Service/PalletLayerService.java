package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.PalletLayerRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PalletLayerService {

    @Autowired
    private PalletLayerRepository palletLayerRepository;
    private final ReentrantLock lock = new ReentrantLock();
    Logger logger = LoggerFactory.getLogger(WorkLogService.class);

    /**
     * Aktualisiert die Palettenposition: Verschiebt teilweise oder gesamten Bestand vom alten zum neuen Lagerplatz
     */
    @Transactional
    public ApiResponse<?> updatePalett(String oldBinCode, String newBinCode, int itemId, int unitCount) {

        lock.lock();
        try {
            // Bestand am alten Lagerplatz abrufen
            Integer oldStock = palletLayerRepository.getStock(itemId, oldBinCode);
            int currentOldStock = (oldStock != null) ? oldStock : 0;

            // Prüfen, ob genügend Bestand zum Verschieben vorhanden ist
            if (currentOldStock > unitCount) {
                palletLayerRepository.minusStock(itemId, unitCount, oldBinCode, currentOldStock);
                logger.info("Teilweise Verschiebung vom alten Lagerplatz: itemId={}, oldBinCode={}, Menge={}, aktueller Bestand={}", itemId, oldBinCode, unitCount, currentOldStock);
            } else if (currentOldStock == unitCount) {
                palletLayerRepository.changePalettStatusInto49(itemId, oldBinCode);
                palletLayerRepository.deleteItemId49();
                logger.info("Gesamter Bestand vom alten Lagerplatz verschoben, Status auf 49 gesetzt und Eintrag gelöscht: itemId={}, binCode={}", itemId, oldBinCode);
            } else {
                logger.warn("Unzureichender Bestand: Verschiebung vom alten Lagerplatz nicht möglich. itemId={}, oldBinCode={}, angefragte Menge={}, tatsächlicher Bestand={}",
                        itemId, oldBinCode, unitCount, currentOldStock);
                throw new BusinessException("Unzureichender Lagerbestand");
            }

            // Bestand am neuen Lagerplatz abrufen
            Integer newStock = palletLayerRepository.getStock(itemId, newBinCode);
            int currentNewStock = (newStock != null) ? newStock : 0;

            // Bestand zum neuen Lagerplatz hinzufügen
            if (currentNewStock != 0) {
                palletLayerRepository.addStock(currentNewStock, itemId, unitCount, newBinCode);
                logger.info("Bestand zum vorhandenen neuen Lagerplatz hinzugefügt: itemId={}, newBinCode={}, Menge hinzugefügt={}, vorheriger Bestand={}", itemId, newBinCode, unitCount, currentNewStock);
            } else {
                palletLayerRepository.insertStockPalett(itemId, unitCount, newBinCode);
                palletLayerRepository.insertStockBin(newBinCode);
                logger.info("Neuen Lagerplatz angelegt und Bestand eingefügt: itemId={}, newBinCode={}, Menge={}", itemId, newBinCode, unitCount);
            }

            return ApiResponse.success("Palettenposition erfolgreich verschoben", null);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Löscht alle Paletten-Daten von einem angegebenen Lagerplatz.
     *
     * @param binCode Lagerplatz-Code
     * @return Antwort mit dem Ergebnis der Operation
     */
    @Transactional
    @CacheEvict(value = "allStock", allEntries = true)
    public ApiResponse<?> deleteAllPalettFromBin(String binCode) {
        palletLayerRepository.deleteAllPalettFromBin(binCode);
        palletLayerRepository.deleteBin(binCode);
        logger.info("Lagerplatz erfolgreich geleert: {}", binCode);
        return ApiResponse.success("Lagerplatz erfolgreich geleert", null);
    }

    /**
     * Sucht alle Artikel an einem bestimmten Lagerplatz.
     */
    public ApiResponse<?> searchAllItemFromBin(String binCode) {
        List<Object[]> allItemFromBin = palletLayerRepository.searchAllItemFromBin(binCode);
        logger.info("Lagerplatz-Suche Ergebnisse: {}", allItemFromBin);
        return ApiResponse.success("Lagerplatzsuche erfolgreich", allItemFromBin);
    }
}
