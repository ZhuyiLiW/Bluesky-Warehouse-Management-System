package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Repository.OptimalStorageLocationRepository;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OptimalStorageLocationServiceTest {
    @Mock
    OptimalStorageLocationRepository optimalStorageLocationRepository;
    @InjectMocks
    OptimalStorageLocationService optimalStorageLocationService;
    @Test
    public void getOptimalBin_whenSlotAndBinsExist_thenReturnResponse() {
        // Arrange
        when(optimalStorageLocationRepository.getOptimalSlot(anyInt()))
                .thenReturn("P1");
        when(optimalStorageLocationRepository.getOptimalBinlist(eq("P1")))
                .thenReturn(Arrays.asList("bin1", "bin2"));

        // Act
        ApiResponse<?> getOptimalBinTest = assertDoesNotThrow(
                () -> optimalStorageLocationService.getOptimalBin(1)
        );

        // Assert
        assertEquals(getOptimalBinTest.getMessage(), "Erfolgreich optimale Lagerplätze abgerufen");
        verify(optimalStorageLocationRepository, times(1)).getOptimalSlot(anyInt());
        verify(optimalStorageLocationRepository, times(1)).getOptimalBinlist(eq("P1"));
    }

    @Test
    public void getOptimalBin_whenNoSlotOrBins_thenThrowBusinessException() {
        // Arrange
        when(optimalStorageLocationRepository.getOptimalSlot(eq(2)))
                .thenReturn("");
        when(optimalStorageLocationRepository.getOptimalBinlist(null))
                .thenReturn(null);

        // Act + Assert
        assertThrows(BusinessException.class,
                () -> optimalStorageLocationService.getOptimalBin(2));

        verify(optimalStorageLocationRepository, times(1)).getOptimalSlot(anyInt());
        verify(optimalStorageLocationRepository, times(1)).getOptimalBinlist(eq(""));
    }

    @Test
   public void whenEmptyBinsExist_thenReturnApiResponse() {
        // Arrange
        int itemId = 1;
        String slot = "P1";
        List<String> emptyBins = Arrays.asList("bin1", "bin2");
        when(optimalStorageLocationRepository.getOneOptimalSlot(itemId)).thenReturn(slot);
        when(optimalStorageLocationRepository.getAllEmptyBinListBySlot(slot)).thenReturn(emptyBins);

        // Act
        assertDoesNotThrow(() -> optimalStorageLocationService.getAllEmptyBin(itemId));

        // Assert
        verify(optimalStorageLocationRepository, times(1)).getOneOptimalSlot(itemId);
        verify(optimalStorageLocationRepository, times(1)).getAllEmptyBinListBySlot(slot);
    }

    @Test
   public void whenEmptyBinsNotFound_thenThrowBusinessException() {
        // Arrange
        int itemId = 2;
        String slot = "P2";
        when(optimalStorageLocationRepository.getOneOptimalSlot(itemId)).thenReturn(slot);
        when(optimalStorageLocationRepository.getAllEmptyBinListBySlot(slot))
                .thenReturn(Collections.emptyList());

        // Act + Assert
        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> optimalStorageLocationService.getAllEmptyBin(itemId)
        );
        assertEquals("Systemfehler: Kein geeigneter Lagerplatz gefunden", ex.getMessage());
    }



}
