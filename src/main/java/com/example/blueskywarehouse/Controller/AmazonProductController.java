package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.AmazonProductService;
import com.example.blueskywarehouse.Service.ItemManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/AmazonProductController")
public class AmazonProductController {
    @Autowired
    private AmazonProductService amazonProductService;
    @PreAuthorize("hasRole('1') ")
    @GetMapping("/getNewBarCode")
    public ApiResponse<?> getNewBarCode(@RequestParam String oldBarCode){
        return amazonProductService.getNewBarCode(oldBarCode);
    }
}
