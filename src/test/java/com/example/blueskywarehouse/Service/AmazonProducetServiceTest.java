package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Repository.AmazonProductRepository;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AmazonProducetServiceTest {
    @Mock
    private AmazonProductRepository amazonProductRepository;
    @InjectMocks
    AmazonProductService amazonProductService;

    @Test
    public void shouldReturnNewBarCode_whenOldBarCodeIsValid() {
        // Arrange
        String oldBarCode = "Xfq12hdhq";
        String newBarCode = "Xfq12hdhqNew";
        when(amazonProductRepository.getNewBarCode(oldBarCode)).thenReturn(newBarCode);

        // Act
        ApiResponse<Object> response = amazonProductService.getNewBarCode(oldBarCode);

        // Assert
        assertEquals("Neuer Barcode erfolgreich erhalten.", response.getMessage());
        assertEquals(newBarCode, response.getData());
        verify(amazonProductRepository, times(1)).getNewBarCode(oldBarCode);
    }

    @Test
    public void shouldThrowException_whenOldBarCodeIsNullOrEmpty() {
        // Act & Assert
        assertThrows(BusinessException.class, () -> amazonProductService.getNewBarCode(""));
        assertThrows(BusinessException.class, () -> amazonProductService.getNewBarCode(null));

        // Assert (verify repository never called)
        verify(amazonProductRepository, never()).getNewBarCode(anyString());
    }

    @Test
    public void shouldThrowException_whenNewBarCodeNotFound() {
        // Arrange
        String oldBarCode = "Xfq12hdhq";
        when(amazonProductRepository.getNewBarCode(oldBarCode)).thenReturn(null);

        // Act & Assert
        assertThrows(BusinessException.class, () -> amazonProductService.getNewBarCode(oldBarCode));

        // Assert (verify repository called once)
        verify(amazonProductRepository, times(1)).getNewBarCode(oldBarCode);
    }

    @Test
    public void shouldThrowException_whenNewBarCodeIsBlank() {
        // Arrange
        String oldBarCode = "Xfq12hdhq";
        when(amazonProductRepository.getNewBarCode(oldBarCode)).thenReturn("   ");

        // Act & Assert
        assertThrows(BusinessException.class, () -> amazonProductService.getNewBarCode(oldBarCode));

        // Assert (verify repository called once)
        verify(amazonProductRepository, times(1)).getNewBarCode(oldBarCode);
    }

}
