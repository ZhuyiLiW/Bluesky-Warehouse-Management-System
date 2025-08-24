package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.OptimalStorageLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/optimalStorageLocationController")
public class OptimalStorageLocationController {

    @Autowired
    private OptimalStorageLocationService optimalStorageLocationService;

    // Optimalen Lagerplatz für einen Artikel abrufen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping("/getOptimalBinList")
    public ApiResponse<?> getOneOptimalSlot(int itemId){
        return optimalStorageLocationService.getOptimalBin(itemId);
    }

    // Alle leeren Lagerplätze für einen Artikel abrufen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping("/getAllEmptyBinList")
    public ApiResponse<?> getAllEmptyBinList(int itemId){
        return optimalStorageLocationService.getAllEmptyBin(itemId);
    }

    // Paletten in einen neuen Lagerplatz verschieben
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/insertPalletsIntoNewBin")
    public ApiResponse<?> insertPalletsIntoNewBin(String newBinCode, String oldBinCode){
        return optimalStorageLocationService.InsertPalletsIntoNewBin(newBinCode, oldBinCode);
    }
}
