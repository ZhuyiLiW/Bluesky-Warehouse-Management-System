package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dto.PriceListDto;
import com.example.blueskywarehouse.Repository.PriceListRepository;
import com.example.blueskywarehouse.Entity.PriceList;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Exception.InvalidParameterException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PriceListService {
    @Autowired
    private PriceListRepository priceListRepository;
    Logger logger = LoggerFactory.getLogger(OptimalStorageLocationService.class);

    /**
     * Fügt einen neuen Preiseintrag in die Preis- und Preishistorientabellen ein.
     * <p>
     * Diese Methode führt zwei Operationen aus:
     * 1. Fügt den aktuellen Preis in die Tabelle price_list ein (für aktuelle Preisabfragen)
     * 2. Fügt gleichzeitig den Preis in die Tabelle price_history ein (für Preisverlaufsgrafiken)
     *
     * @param itemId Die eindeutige Produkt-ID, muss größer als 0 sein
     * @param price  Der aktuelle Produktpreis, muss eine nicht-negative Zahl sein
     */
    @Transactional
    @CacheEvict(value = "showPriceList", allEntries = true)
    public ApiResponse<?> insertPriceList(int itemId, double price, String remark) {
        // Prüfen ob itemId bereits existiert
        List<Integer> isItemIdExisted = priceListRepository.checkItemId(itemId);
        if (isItemIdExisted.size() > 0) {
            logger.warn("itemId existiert bereits itemId={}, price={}", itemId, price);
            throw new BusinessException("Produktpreis existiert bereits");
        }
        // Parameterprüfung (optional)
        if (itemId <= 0 || price < 0) {
            logger.warn("Preis-Einfügung fehlgeschlagen: ungültige Parameter itemId={}, price={}", itemId, price);
            throw new InvalidParameterException("Ungültige Parameter");
        }

        LocalDate date = LocalDate.now();
        // Aktuellen Preis einfügen
        PriceList priceList=new PriceList();
        priceList.setItemId(itemId);
        priceList.setPrice(String.valueOf(price));
        priceList.setRemark(remark);
        priceListRepository.save(priceList);

        // Preis-Historie aufzeichnen (für Verlaufsgrafik)

        priceListRepository.insertPriceListHistory(itemId, price, date);

        logger.info("Preis und Historie erfolgreich eingefügt, itemId={}, price={}, date={}", itemId, price, date);
        return ApiResponse.success("Preis erfolgreich eingefügt", null);
    }

    /**
     * Aktualisiert den Preis eines Produkts und zeichnet die Preisänderung historisch auf.
     * <p>
     * Diese Methode führt zwei Datenbankoperationen aus:
     * 1. Aktualisiert den aktuellen Preis in price_list für die angegebene itemId
     * 2. Fügt einen Eintrag in price_history ein, der die Änderung dokumentiert (für Verlaufsgrafiken)
     *
     * @param itemId Die eindeutige Produkt-ID, muss größer als 0 sein
     * @param price  Der neue Produktpreis, muss nicht-negativ sein
     */
    @Transactional
    @CacheEvict(value = "showPriceList", allEntries = true)
    public ApiResponse<?> updatePriceList(int itemId, double price) {
        LocalDate date = LocalDate.now();

        // Parameterprüfung (optional)
        if (itemId <= 0 || price < 0) {
            logger.warn("Preis-Aktualisierung fehlgeschlagen: ungültige Parameter itemId={}, price={}", itemId, price);
            throw new InvalidParameterException("Ungültige Parameter");
        }

        // Aktuellen Preis aktualisieren
        priceListRepository.updatePriceList(itemId, String.valueOf(price));

        // Historischen Preis einfügen (für Preisverlaufsgrafik)
        priceListRepository.insertPriceListHistory(itemId, price, date);

        logger.info("Produktpreis erfolgreich aktualisiert itemId={}, neuerPreis={}, date={}", itemId, price, date);
        return ApiResponse.success("Preis erfolgreich aktualisiert", null);
    }


    /**
     * Abfrage und Rückgabe der Preisliste aller Produkte.
     * <p>
     * Diese Methode liest alle Datensätze aus der Tabelle price_list aus und gibt sie für die Anzeige an das Frontend zurück.
     */
    @Cacheable(value = "showPriceList")
    public ApiResponse<?> showPriceList() {
        List<PriceListDto> priceList = priceListRepository.searchPriceList();

        if (priceList == null || priceList.isEmpty()) {
            logger.warn("Preisliste abgefragt: keine Daten gefunden");
            throw new BusinessException("Keine Preishistorie für das Produkt gefunden");
        }

        logger.info("Alle Produktpreise erfolgreich abgefragt, Anzahl der Einträge: {}", priceList.size());
        return ApiResponse.success("Alle Produktpreise wie folgt:", priceList);
    }

    /**
     * Abfrage der Preishistorie für ein bestimmtes Produkt.
     * <p>
     * Dient zur Erstellung einer Preisverlaufsgrafik, Daten stammen aus der Tabelle price_history.
     *
     * @param itemId Die eindeutige Produkt-ID, muss größer als 0 sein
     */
    public ApiResponse<?> showPriceListHistory(int itemId) {
        List<PriceList> priceHistoryList = priceListRepository.searchPriceHistory(itemId);

        if (priceHistoryList == null || priceHistoryList.isEmpty()) {
            logger.warn("Keine Preishistorie für Produkt ID={} gefunden", itemId);
            throw new BusinessException("Keine Preishistorie für das Produkt gefunden");
        }

        logger.info("Preishistorie für Produkt ID={} erfolgreich abgefragt, insgesamt {} Einträge", itemId, priceHistoryList.size());
        return ApiResponse.success("Preisverlaufsgrafik erfolgreich abgerufen", priceHistoryList);
    }

}
