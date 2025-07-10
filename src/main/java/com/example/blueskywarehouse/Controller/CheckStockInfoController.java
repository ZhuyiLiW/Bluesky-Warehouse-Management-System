package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.CheckStockInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/CheckStockInfoController")
public class CheckStockInfoController {
    @Autowired
    private CheckStockInfoService checkStockInfoService;
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/getAllStockInfo")
    public ApiResponse<?> getAllStockInfo(){
        return checkStockInfoService.getAllStockInfo();
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/getAllStockInfoLocation")
    public ApiResponse<?> getAllStockInfoLocation(){
        return checkStockInfoService.getAllStockLocation();
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/getAllBincodeByItemId")
    public ApiResponse<?> getAllBincodeByItemId(int itemId){
        return checkStockInfoService.getLocationByItemId(itemId);
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/updatePalletinfoById")
    public ApiResponse<?> updatePalletinfoById(int id,int itemId,int boxStock,int unitStock){
        return checkStockInfoService.updatePalletinfoById(id,itemId,boxStock,unitStock);
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/deletePalletinfoById")
    public ApiResponse<?> deletePalletinfoById(int id){
        return checkStockInfoService.deletePalletinfoById(id);
    }
}
