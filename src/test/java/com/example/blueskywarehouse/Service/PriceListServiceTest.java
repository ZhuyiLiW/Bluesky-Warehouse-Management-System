package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Repository.PriceListRepository;
import com.example.blueskywarehouse.Entity.PriceList;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PriceListServiceTest {
    @Mock
    private PriceListRepository priceListRepository;

    @InjectMocks
    private PriceListService priceListService;

    final int itemId = 5;
    final double price = 10.23;
    final String remark = "Testbemerkung";
    LocalDate date = LocalDate.now();





}
