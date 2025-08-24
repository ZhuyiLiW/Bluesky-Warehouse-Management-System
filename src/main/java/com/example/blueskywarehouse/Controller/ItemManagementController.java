package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.CheckStockInfoService;
import com.example.blueskywarehouse.Service.ItemManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ItemManagementController")
public class ItemManagementController {

    @Autowired
    private ItemManagementService itemManagementService;

    // Neues Artikel hinzufügen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/addItem")
    public ApiResponse<?> addItem(String name, String type, Integer unitPerBox, String productGroup){
        return itemManagementService.addItem(name,type,unitPerBox,productGroup);
    }

    // Artikel aktualisieren
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PutMapping("/updateItem")
    public ApiResponse<?> updateItem(Integer id,String name, String type,Integer unitPerBox,String productGroup){
        return itemManagementService.updateItem(id,name,type,unitPerBox,productGroup);
    }

    // Artikel anhand des Namens suchen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping("/searchItem")
    public ApiResponse<?> searchItem(String name){
        return itemManagementService.searchItem(name);
    }

    // Lagerplatz-Informationen eines Artikels abrufen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping("/searchItemLocation")
    public ApiResponse<?> searchItemLocation(int itemId){
        return itemManagementService.searchItemLocation(itemId);
    }

    // Bestand eines Artikels abrufen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping("/searchItemCount")
    public ApiResponse<?> searchItemCount(int itemId){
        return itemManagementService.searchItemCount(itemId);
    }
}
