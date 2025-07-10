package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.AmazonProductService;
import com.example.blueskywarehouse.Service.ItemManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/AmazonProductController")
public class AmazonProductController {
    @Autowired
    private AmazonProductService amazonProductService;
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/getNewBarCode")
    public ApiResponse<?> getNewBarCode(String oldBarCode){
        return amazonProductService.getNewBarCode(oldBarCode);
    }
}
