package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.CheckStockInfoService;
import com.example.blueskywarehouse.Service.ItemManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/ItemManagementController")
public class ItemManagementController {
    @Autowired
    private ItemManagementService itemManagementService;
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/addItem")
    public ApiResponse<?> addItem(String name, String type, Integer unitPerBox, String productGroup){
        return itemManagementService.addItem(name,type,unitPerBox,productGroup);
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/updateItem")
    public ApiResponse<?> updateItem(Integer id,String name, String type,Integer unitPerBox,String productGroup){
        return itemManagementService.updateItem(id,name,type,unitPerBox,productGroup);
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/searchItem")
    public ApiResponse<?> searchItem(String name){
        return itemManagementService.searchItem(name);
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/searchItemLocation")
    public ApiResponse<?> searchItemLocation(int itemId){
        return itemManagementService.searchItemLocation(itemId);
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/searchItemCount")
    public ApiResponse<?> searchItemCount(int itemId){
        return itemManagementService.searchItemCount(itemId);
    }


}
