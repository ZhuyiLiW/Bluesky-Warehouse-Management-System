package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Dao.PackingInfoRepository;
import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.AmazonProductService;
import com.example.blueskywarehouse.Service.PackingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/PackingInfoController")
public class PackingInfoController {
    @Autowired
    private PackingInfoService packingInfoService;
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/getNewBarCode")
    public ApiResponse<?>  insertPackingInfo(String packingDate,String packingNumber,String customerName,String oldSku,String newSku,int quantity,double weight,String cartonSize,int operationNumber,int isWeightConfirmed) {
        return packingInfoService.insertPackingInfo(packingDate,packingNumber,customerName, oldSku,newSku, quantity, weight, cartonSize,operationNumber,isWeightConfirmed);
    }
}
