package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.ItemManagementRepository;
import com.example.blueskywarehouse.Entity.Item;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Produktverwaltungsservice, verantwortlich für die Verarbeitung der geschäftlichen Logik rund um Produkte wie Hinzufügen, Aktualisieren und Abfragen von Produktinformationen.
 */
@Service
public class ItemManagementService {

    @Autowired
    private ItemManagementRepository itemManagementRepository;

    Logger logger = LoggerFactory.getLogger(ItemManagementService.class);

    /**
     * Einen neuen Produkteintrag hinzufügen. Falls es sich um Kundenlagerbestand handelt, wird der Artikelname als Kundenname + Produktname definiert, und der Typ entspricht dem Kundennamen.
     */
    @Transactional
    public ApiResponse<?> addItem(String name, String type, Integer unitPerBox, String productGroup) {
        itemManagementRepository.addItem(name, type, unitPerBox, productGroup);
        return ApiResponse.success("Neues Produkt erfolgreich hinzugefügt",null);
    }

    /**
     * Bestehendes Produktname und Typ aktualisieren.
     */
    @Transactional
    public ApiResponse<?> updateItem(int id, String name, String type, int unitPerBox,String productGroup) {
        Item item=itemManagementRepository.findById((long) id).orElseThrow(() -> new RuntimeException("Paletteninformation nicht gefunden, id=" + id));
        item.setName(name);
        item.setUnitPerBox(unitPerBox);
        item.setProductGroup(productGroup);
        itemManagementRepository.save(item);
        return ApiResponse.success("Produkt erfolgreich aktualisiert",null);
    }

    /**
     * Produkte anhand des Namens per Like-Suche suchen.
     */
    public ApiResponse<?> searchItem(String name) {
        name = name == null ? "" : name.trim();
        List<Item> itemsList = Optional.ofNullable(itemManagementRepository.searchItem(name))
                .orElse(Collections.emptyList());
        if(itemsList.size()==0)throw new BusinessException("Material existiert nicht");
        return ApiResponse.success("Produktdetails erfolgreich abgerufen", itemsList);
    }

    /**
     * Lagerort eines Produkttyps abfragen.
     */
    public ApiResponse<?> searchItemLocation(int itemId) {
        List<String> itemsLocationList = itemManagementRepository.searchItemLocation(itemId);
        return ApiResponse.success("Produktstandorte erfolgreich abgerufen", itemsLocationList);
    }
    /**
     * Menge eines Produkttyps abfragen: itemsCount[0] = Kartonanzahl, itemsCount[1] = Stückzahl
     */
    public ApiResponse<?> searchItemCount(int itemId) {
        Object[] itemCount =itemManagementRepository.searchItemCount(itemId);
        return ApiResponse.success("Produktmengen erfolgreich abgerufen", itemCount);

    }

}
