package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.PalletLayerService;
import com.example.blueskywarehouse.Service.WorkLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/palletlayer")
public class PalletLayerController {

    @Autowired
    private PalletLayerService palletLayerService;

    // Palette von einem Lagerplatz zu einem anderen verschieben
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/movePalett")
    public ApiResponse<?> movePalett(String oldBinCode, String newBinCode, int itemId, int unitCount) {
        return palletLayerService.updatePalett(oldBinCode, newBinCode, itemId, unitCount);
    }

    // Alle Paletten aus einem bestimmten Lagerplatz löschen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @DeleteMapping("/deleteAllPalettFromBin")
    public ApiResponse<?> deleteAllPalettFromBin(String binCode) {
        return palletLayerService.deleteAllPalettFromBin(binCode);
    }

    // Alle Artikel aus einem bestimmten Lagerplatz abrufen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping("/searchAllItemFromBin")
    public ApiResponse<?> searchAllItemFromBin(String binCode) {
        return palletLayerService.searchAllItemFromBin(binCode);
    }
}

