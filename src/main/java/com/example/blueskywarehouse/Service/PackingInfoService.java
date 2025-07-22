package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Dao.AmazonProductRepository;
import com.example.blueskywarehouse.Dao.OptimalStorageLocationRepository;
import com.example.blueskywarehouse.Dao.PackingInfoRepository;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackingInfoService {
    @Autowired
    private PackingInfoRepository packingInfoRepository;
    @Autowired
    private AmazonProductRepository amazonProductRepository;
    private final int weightConfirmed = 0;
    private final int weightNotConfirmed = 1;

    Logger logger = LoggerFactory.getLogger(OptimalStorageLocationService.class);

    public ApiResponse<?> insertPackingInfo(String packingDate, String packingNumber, String customerName, String oldSku, String newSku, int quantity, double weight, String cartonSize, int operationNumber, int isWeightConfirmed) {

        String isPackingNumberExisted = packingInfoRepository.getPackingNumber(packingNumber);
        if (isPackingNumberExisted != "" && !isPackingNumberExisted.isEmpty())
            throw new BusinessException("Packnummer existiert bereits");
        Integer isCustomerExisted = amazonProductRepository.getCustomerByName(customerName);
        if (isCustomerExisted == null)
            throw new BusinessException("Bitte überprüfen Sie den Kundennamen, dieser Kunde existiert nicht");
        String getRealNewSku = amazonProductRepository.getNewBarCode(oldSku);
        if (!getRealNewSku.equals(newSku))
            throw new BusinessException("Neues Etikett ist fehlerhaft, bitte überprüfen");
        double realWeight = amazonProductRepository.getWeight(customerName);
        if ((realWeight * quantity > 20 || weight > 20) && isWeightConfirmed == weightNotConfirmed)
            throw new BusinessException("Gesamtgewicht überschreitet das Limit, bitte prüfen Sie das Gewicht oder ändern Sie das Gewicht pro Produkt");
        Integer isOperationNumber = amazonProductRepository.getOperationNumber(isCustomerExisted);
        if (isOperationNumber != operationNumber)
            throw new BusinessException("Fehler bei der Operation, bitte überprüfen Sie die Anforderungen");
        packingInfoRepository.insertPackingInfo(packingDate, packingNumber, isCustomerExisted, oldSku, newSku, quantity, weight, cartonSize, operationNumber, isWeightConfirmed);
        return ApiResponse.success("Verpackungsinformationen erfolgreich eingefügt", null);
    }
}
