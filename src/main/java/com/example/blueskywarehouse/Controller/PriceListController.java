package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.PriceListService;
import com.example.blueskywarehouse.Service.WorkLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pricelist")
public class PriceListController {

    @Autowired
    private PriceListService priceListService;
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/addPriceList")
    public ApiResponse<?> insertPriceList(@RequestParam int itemId, @RequestParam double price,@RequestParam String remark) {
        return priceListService.insertPriceList( itemId, price,remark);
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/updatePriceList")
    public ApiResponse<?> updatePriceList(@RequestParam int itemId, @RequestParam double price) {
        return priceListService.updatePriceList( itemId, price);
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/showPriceList")
    public ApiResponse<?> showPriceList() {
        return priceListService.showPriceList();
    }
    @PreAuthorize("hasRole('3')")
    @PostMapping("/showPriceListHistory")
    public ApiResponse<?> showPriceListHistory(@RequestParam int itemId) {
        return priceListService.showPriceListHistory( itemId);
    }
}
