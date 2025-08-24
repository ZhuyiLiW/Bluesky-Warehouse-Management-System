package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.CheckStockInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/CheckStockInfoController")

public class CheckStockInfoController {

    @Autowired
    private CheckStockInfoService checkStockInfoService;

    // Alle Lagerbestände abrufen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping("/getAllStockInfo")
    public ApiResponse<?> getAllStockInfo(){
        return checkStockInfoService.getAllStockInfo();
    }

    // Alle Lagerplatz-Informationen abrufen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping("/getAllStockInfoLocation")
    public ApiResponse<?> getAllStockInfoLocation(){
        return checkStockInfoService.getAllStockLocation();
    }

    // Alle BinCodes zu einer Artikel-ID abrufen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping("/getAllBincodeByItemId")
    public ApiResponse<?> getAllBincodeByItemId(int itemId){
        return checkStockInfoService.getLocationByItemId(itemId);
    }

    // Paletteninformationen nach ID aktualisieren
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PutMapping("/updatePalletinfoById")
    public ApiResponse<?> updatePalletinfoById(int id,int itemId,int boxStock,int unitStock){
        return checkStockInfoService.updatePalletinfoById(id,itemId,boxStock,unitStock);
    }

    // Palette nach ID löschen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @DeleteMapping("/deletePalletinfoById")
    public ApiResponse<?> deletePalletinfoById(int id){
        return checkStockInfoService.deletePalletinfoById(id);
    }
}
