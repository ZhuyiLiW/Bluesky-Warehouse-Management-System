package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.PalletLayerRepository;
import com.example.blueskywarehouse.Dao.PriceListRepository;
import com.example.blueskywarehouse.Entity.PriceList;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Array;
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

    @Test
    void insertPriceList() {
        ApiResponse<?> response = priceListService.insertPriceList(itemId, price, remark);
        assertEquals("Preis erfolgreich eingefügt", response.getMessage());
        verify(priceListRepository).insertPriceList(itemId, price, remark);
        verify(priceListRepository, times(1)).insertPriceList(itemId, price, remark);
    }

    @Test
    void updatePriceList() {
        ApiResponse<?> response = priceListService.updatePriceList(itemId, price);
        assertEquals("Preis erfolgreich aktualisiert", response.getMessage());
        verify(priceListRepository).updatePriceList(itemId, price);
        verify(priceListRepository, times(1)).updatePriceList(itemId, price);
    }

    @Test
    void showPriceList() {
        List<PriceList> mockData = Arrays.asList(
                new PriceList(1, "Test", "2.34", "Testbemerkung"),
                new PriceList(2, "Test", "3.34", "Testbemerkung"),
                new PriceList(3, "Test", "2.64", "Testbemerkung")
        );
        when(priceListRepository.searchPriceList()).thenReturn(mockData);
        ApiResponse<?> response = priceListService.showPriceList();
        assertEquals("Alle Produktpreise wie folgt:", response.getMessage());
    }
}
