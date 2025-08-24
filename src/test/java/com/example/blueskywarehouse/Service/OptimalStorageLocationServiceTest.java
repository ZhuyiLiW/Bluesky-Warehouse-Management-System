package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Repository.OptimalStorageLocationRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OptimalStorageLocationServiceTest {

    @Mock
    private OptimalStorageLocationRepository optimalStorageLocationRepository;

    @InjectMocks
    private OptimalStorageLocationService optimalStorageLocationService;

    @Test
    void getOptimalBinTest() {
        String mockOptimalSlot = "Ptest";
        List<String> mockBinList = Arrays.asList("Ptest-01-1", "Ptest-02-1", "Ptest-03-1");

        // Simuliere Rückgabe von optimalem Slot und Bin-Liste
        when(optimalStorageLocationRepository.getOptimalSlot(3)).thenReturn(mockOptimalSlot);
        when(optimalStorageLocationRepository.getOptimalBinlist(mockOptimalSlot)).thenReturn(mockBinList);

        ApiResponse<?> response = optimalStorageLocationService.getOptimalBin(3);

        // Erfolgreiche Rückmeldung prüfen
        assertEquals("Optimale Lagerplätze erfolgreich abgerufen", response.getMessage());

        List<String> result = (List<String>) response.getData();
        assertEquals("Ptest-03-1", result.get(2));

        // Simuliere leere Rückgabe (Fehlerfall)
        when(optimalStorageLocationRepository.getOptimalBinlist(mockOptimalSlot)).thenReturn(Collections.emptyList());

        // Erwartete Ausnahme prüfen
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            optimalStorageLocationService.getOptimalBin(3);
        });

        assertEquals("Systemfehler: Kein geeigneter Lagerplatz gefunden", exception.getMessage());
    }

    @Test
    void InsertPalletsIntoNewBin() {
        String newBinCode = "NewTest-01-01";
        String oldBinCode = "OldTest-01-01";
        String newSlotCode = newBinCode.split("-")[0];

        // Slot-Code korrekt extrahiert?
        assertEquals("NewTest", newSlotCode);

        ApiResponse<?> response = optimalStorageLocationService.InsertPalletsIntoNewBin(newBinCode, oldBinCode);

        // Erfolgsnachricht prüfen
        assertEquals("Lagerplatz erfolgreich verschoben", response.getMessage());
    }
}
