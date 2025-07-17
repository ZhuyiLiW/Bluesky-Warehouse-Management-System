package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.OptimalStorageLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/optimalStorageLocationController")
public class OptimalStorageLocationController {
    @Autowired
    private OptimalStorageLocationService optimalStorageLocationService;
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/getOptimalBinList")
    public ApiResponse<?> getOneOptimalSlot(int itemId){
        return optimalStorageLocationService.getOptimalBin(itemId);
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/getAllEmptyBinList")
    public ApiResponse<?> getAllEmptyBinList(int itemId){
        return optimalStorageLocationService.getAllEmptyBin(itemId);
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/insertPalletsIntoNewBin")
    public ApiResponse<?> insertPalletsIntoNewBin(String newBinCode,String oldBinCode){
        return optimalStorageLocationService.InsertPalletsIntoNewBin(newBinCode,oldBinCode);
    }
}
