package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dto.ItemCountDto;
import com.example.blueskywarehouse.Repository.ItemManagementRepository;
import com.example.blueskywarehouse.Entity.Item;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Util.SqlLikeEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ItemManagementService {

    @Autowired
    private ItemManagementRepository itemManagementRepository;

    Logger logger = LoggerFactory.getLogger(ItemManagementService.class);

    /**
     * Neues Produkt in das System einfügen
     */
    @Transactional
    @CacheEvict(value = "allStock", allEntries = true)
    public ApiResponse<?> addItem(String name, String type, Integer unitPerBox, String productGroup) {
        logger.info("addItem() aufgerufen mit name={}, type={}, unitPerBox={}, productGroup={}", name, type, unitPerBox, productGroup);
        logger.debug("Versuche neues Produkt in die Datenbank einzufügen");
        Item item = new Item();
        item.setName(name);
        item.setType(type);
        item.setUnitPerBox(unitPerBox);
        item.setProductGroup(productGroup);
        itemManagementRepository.save(item);
        return ApiResponse.success("Neues Produkt erfolgreich hinzugefügt", null);
    }

    /**
     * Vorhandenes Produkt anhand der ID aktualisieren
     */
    @Transactional
    @CacheEvict(value = "allStock", allEntries = true)
    public ApiResponse<?> updateItem(int id, String name, String type, int unitPerBox, String productGroup) {
        logger.info("updateItem() aufgerufen mit id={}, name={}, type={}, unitPerBox={}, productGroup={}", id, name, type, unitPerBox, productGroup);
        logger.debug("Lade Produkt mit ID {} zur Aktualisierung", id);
        Item item = itemManagementRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Paletteninformation nicht gefunden, id=" + id));
        item.setName(name);
        item.setUnitPerBox(unitPerBox);
        item.setProductGroup(productGroup);
        itemManagementRepository.save(item);
        logger.debug("Produkt gespeichert: {}", item);
        return ApiResponse.success("Produkt erfolgreich aktualisiert", null);
    }

    /**
     * Produkt anhand des Namens suchen
     */
    public ApiResponse<?> searchItem(String name) {
        logger.info("searchItem() aufgerufen mit name={}", name);
        if(name.isEmpty()){
            logger.info("searchItem() aufgerufen, aber Name ist leer oder null");
            throw new BusinessException("Name kann nicht leer sein");
        }
        SqlLikeEscaper.escape(name);
        List<Item> itemsList = Optional.ofNullable(itemManagementRepository.searchItem(name))
                .orElse(Collections.emptyList());
        logger.debug("Gefundene Items: {}", itemsList);
        if (itemsList.size() == 0) throw new BusinessException("Material existiert nicht");
        return ApiResponse.success("Produktdetails erfolgreich abgerufen", itemsList);
    }

    /**
     * Lagerorte eines Produkts anhand der Artikel-ID abrufen
     */
    public ApiResponse<?> searchItemLocation(int itemId) {
        logger.info("searchItemLocation() aufgerufen mit itemId={}", itemId);
        List<String> itemsLocationList = itemManagementRepository.searchItemLocation(itemId);
        logger.debug("Gefundene Standorte: {}", itemsLocationList);
        return ApiResponse.success("Produktstandorte erfolgreich abgerufen", itemsLocationList);
    }

    /**
     * Bestandsmengen (Kartons und Stücke) eines Produkts anhand der Artikel-ID abrufen
     */
    public ApiResponse<?> searchItemCount(int itemId) {
        logger.info("searchItemCount() aufgerufen mit itemId={}", itemId);
        ItemCountDto itemCount = itemManagementRepository.searchItemCount(itemId);
        logger.debug("Gefundene Mengen: Kartons={}, Stück={}", itemCount.boxStock(), itemCount.unitStock());
        return ApiResponse.success("Produktmengen erfolgreich abgerufen", itemCount);
    }


}
