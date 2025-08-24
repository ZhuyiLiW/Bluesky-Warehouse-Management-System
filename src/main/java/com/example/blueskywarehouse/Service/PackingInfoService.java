package com.example.blueskywarehouse.Service;

import com.example.blueskywarehouse.Entity.PackingInfo;
import com.example.blueskywarehouse.Repository.AmazonProductRepository;
import com.example.blueskywarehouse.Repository.PackingInfoRepository;
import com.example.blueskywarehouse.Entity.AmazonProduct;
import com.example.blueskywarehouse.Exception.BusinessException;
import com.example.blueskywarehouse.Response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackingInfoService {

    @Autowired
    private PackingInfoRepository packingInfoRepository;

    @Autowired
    private AmazonProductRepository amazonProductRepository;

    private final int weightConfirmed = 0;
    private final int weightNotConfirmed = 1;

    Logger logger = LoggerFactory.getLogger(PackingInfoService.class);

    /**
     * Fügt neue Verpackungsinformationen ein
     */
    public ApiResponse<?> insertPackingInfo(
            String packingDate,
            String packingNumber,
            String customerName,
            String oldSku,
            String newSku,
            int quantity,
            double weight,
            String cartonSize,
            int operationNumber,
            int isWeightConfirmed
    ) {

        // Prüfen, ob Packnummer bereits existiert
        String isPackingNumberExisted = packingInfoRepository.getPackingNumber(packingNumber);
        if (isPackingNumberExisted != null && !isPackingNumberExisted.isEmpty()) {
            throw new BusinessException("Packnummer existiert bereits");
        }

        // Prüfen, ob Kunde existiert
        Integer isCustomerExisted = amazonProductRepository.getCustomerByName(customerName);
        if (isCustomerExisted == null) {
            throw new BusinessException("Bitte überprüfen Sie den Kundennamen, dieser Kunde existiert nicht");
        }

        // Prüfen, ob neues SKU korrekt ist
        String getRealNewSku = amazonProductRepository.getNewBarCode(oldSku);
        if (!getRealNewSku.equals(newSku)) {
            throw new BusinessException("Neues Etikett ist fehlerhaft, bitte überprüfen");
        }

        // Gewicht prüfen
        Double realWeight = amazonProductRepository.getWeight(customerName);
        if ((realWeight * quantity > 20 || weight > 20) && isWeightConfirmed == weightNotConfirmed) {
            throw new BusinessException("Gesamtgewicht überschreitet das Limit, bitte prüfen Sie das Gewicht oder ändern Sie das Gewicht pro Produkt");
        }

        // Operation-ID prüfen
        Long isOperationNumber = amazonProductRepository.findById(Long.valueOf(isCustomerExisted))
                .map(AmazonProduct::getId)
                .orElse(null);
        if (isOperationNumber != operationNumber) {
            throw new BusinessException("Fehler bei der Operation, bitte überprüfen Sie die Anforderungen");
        }

        // Verpackungsinformationen speichern
        PackingInfo packingInfo = new PackingInfo();
        packingInfo.setDate(packingDate);
        packingInfo.setPackingNumber(packingNumber);
        packingInfo.setOldSku(oldSku);
        packingInfo.setNewSku(newSku);
        packingInfo.setOperationQuantity(quantity);
        packingInfo.setWeight(weight);
        packingInfo.setCarton_size(cartonSize);
        packingInfo.setOperationId(operationNumber);
        packingInfo.setOperationQuantity(isWeightConfirmed); // ⚠️ 这里看起来是笔误

        packingInfoRepository.save(packingInfo);

        return ApiResponse.success("Verpackungsinformationen erfolgreich eingefügt", null);
    }
}
