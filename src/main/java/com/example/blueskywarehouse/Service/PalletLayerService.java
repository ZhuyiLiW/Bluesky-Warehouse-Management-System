package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dto.AllItemFromBinDto;
import com.example.blueskywarehouse.Entity.PalletInfo;
import com.example.blueskywarehouse.Exception.VersionConflictException;
import com.example.blueskywarehouse.Repository.PalletInfoRepository;
import com.example.blueskywarehouse.Repository.PalletLayerRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Repository.StorageSlotRepository;
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

    @Autowired
    private PalletInfoRepository palletInfoRepository;

    @Autowired
    private StorageSlotRepository storageSlotRepository;

    private final ReentrantLock lock = new ReentrantLock();

    Logger logger = LoggerFactory.getLogger(PalletLayerService.class);

    boolean ifUpdateSuccess = false;

    /**
     * Aktualisiert die Palettenposition:
     * Verschiebt teilweise oder gesamten Bestand vom alten zum neuen Lagerplatz
     */
    @Transactional
    public ApiResponse<?> updatePalett(String oldBinCode, String newBinCode, int itemId, int unitCount) {
        lock.lock();
        try {
            // Bestand am alten Lagerplatz abrufen
            Integer oldStock   = palletLayerRepository.getStock(itemId, oldBinCode);
            Integer oldVersion = palletLayerRepository.getVersion(itemId, oldBinCode);

            int currentOldStock = (oldStock != null) ? oldStock : 0;

            // Prüfen, ob genügend Bestand zum Verschieben vorhanden ist
            if (currentOldStock > unitCount) {
                ifUpdateSuccess = palletLayerRepository.minusStock(
                        itemId, unitCount, oldBinCode, currentOldStock, oldVersion
                ) == 0;
                if (ifUpdateSuccess) {
                    throw new VersionConflictException("Aktualisierung fehlgeschlagen, bitte versuchen Sie es erneut.");
                }
                logger.info(
                        "Teilweise Verschiebung vom alten Lagerplatz: itemId={}, oldBinCode={}, Menge={}, aktueller Bestand={}",
                        itemId, oldBinCode, unitCount, currentOldStock
                );

            } else if (currentOldStock == unitCount) {
                ifUpdateSuccess = palletLayerRepository.changePalettStatusInto49(
                        itemId, oldBinCode, oldVersion
                ) == 0;
                if (ifUpdateSuccess) {
                    throw new VersionConflictException("Aktualisierung fehlgeschlagen, bitte versuchen Sie es erneut.");
                }
                palletInfoRepository.deleteItemId49();
                logger.info(
                        "Gesamter Bestand vom alten Lagerplatz verschoben, Status auf 49 gesetzt und Eintrag gelöscht: itemId={}, binCode={}",
                        itemId, oldBinCode
                );

            } else {
                logger.warn(
                        "Unzureichender Bestand: Verschiebung vom alten Lagerplatz nicht möglich. itemId={}, oldBinCode={}, angefragte Menge={}, tatsächlicher Bestand={}",
                        itemId, oldBinCode, unitCount, currentOldStock
                );
                throw new BusinessException("Unzureichender Lagerbestand");
            }

            // Bestand am neuen Lagerplatz abrufen
            Integer newStock   = palletLayerRepository.getStock(itemId, newBinCode);
            Integer newVersion = palletLayerRepository.getVersion(itemId, newBinCode);
            int currentNewStock = (newStock != null) ? newStock : 0;

            // Bestand zum neuen Lagerplatz hinzufügen
            if (currentNewStock != 0) {
                ifUpdateSuccess = palletLayerRepository.addStock(
                        currentNewStock, itemId, unitCount, newBinCode, newVersion
                ) == 0;
                if (ifUpdateSuccess) {
                    throw new VersionConflictException("Aktualisierung fehlgeschlagen, bitte versuchen Sie es erneut.");
                }
                logger.info(
                        "Bestand zum vorhandenen neuen Lagerplatz hinzugefügt: itemId={}, newBinCode={}, Menge hinzugefügt={}, vorheriger Bestand={}",
                        itemId, newBinCode, unitCount, currentNewStock
                );
            } else {
                PalletInfo newPallet = new PalletInfo();
                newPallet.setItemId(itemId);
                newPallet.setUnitStock(unitCount);

                palletInfoRepository.save(newPallet);
                storageSlotRepository.insertStockBin(newBinCode);

                logger.info(
                        "Neuen Lagerplatz angelegt und Bestand eingefügt: itemId={}, newBinCode={}, Menge={}",
                        itemId, newBinCode, unitCount
                );
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
        palletInfoRepository.deleteAllPalettFromBin(binCode);
        storageSlotRepository.deleteBin(binCode);

        logger.info("Lagerplatz erfolgreich geleert: {}", binCode);
        return ApiResponse.success("Lagerplatz erfolgreich geleert", null);
    }

    /**
     * Sucht alle Artikel an einem bestimmten Lagerplatz.
     */
    public ApiResponse<?> searchAllItemFromBin(String binCode) {
        List<AllItemFromBinDto> allItemFromBin = palletInfoRepository.searchAllItemFromBin(binCode);

        logger.info("Lagerplatz-Suche Ergebnisse: {}", allItemFromBin);
        return ApiResponse.success("Lagerplatzsuche erfolgreich", allItemFromBin);
    }
}
