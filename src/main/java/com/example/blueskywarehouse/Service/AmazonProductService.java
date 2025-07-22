package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.AmazonProductRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AmazonProductService {
    @Autowired
    private AmazonProductRepository amazonProductRepository;

    Logger logger = LoggerFactory.getLogger(ItemManagementService.class);

    /**
     * Neuen Produkteintrag hinzufügen. Bei Kundenlager: Artikelname = Kundenname + Produktname, Typ = Kundenname.
     */

    public ApiResponse<Object> getNewBarCode(String oldBarCode) {
        // Parameterprüfung
        if (oldBarCode == null || oldBarCode.trim().isEmpty()) {
            throw new BusinessException("Bitte geben Sie einen gültigen alten Barcode ein.");
        }

        // Neuen Barcode abfragen
        String newBarCode = amazonProductRepository.getNewBarCode(oldBarCode);

        // Überprüfen, ob der neue Barcode existiert
        if (newBarCode == null || newBarCode.trim().isEmpty()) {
            logger.warn("Der alte Barcode [{}] hat keinen zugehörigen neuen Barcode.", oldBarCode);
            throw new BusinessException("Altes Etikett existiert nicht.");
        }

        // Erfolgreiche Antwort zurückgeben
        return ApiResponse.success("Neuer Barcode erfolgreich erhalten.",newBarCode);
    }


    }

