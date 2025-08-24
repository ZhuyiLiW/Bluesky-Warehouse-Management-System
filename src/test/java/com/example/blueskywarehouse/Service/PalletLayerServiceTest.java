package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Repository.PalletLayerRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PalletLayerServiceTest {
    @Mock
    private PalletLayerRepository palletLayerRepository;

    @InjectMocks
    private PalletLayerService palletLayerService;

    private final int itemId = 1;
    private final String oldBinCode = "OLD_BIN";
    private final String newBinCode = "NEW_BIN";

    // Ausreichender Lagerbestand: Teilweise Verschiebung

    // Genauer Lagerbestand: Vollständige Verschiebung


    // Unzureichender Lagerbestand: Ausnahme werfen

}
