package com.example.blueskywarehouse.Repository;

import com.example.blueskywarehouse.Entity.PackingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PackingInfoRepository  extends JpaRepository<PackingInfo, Long> {
    @Query("SELECT p.packingNumber FROM PackingInfo p where p.packingNumber=:packingNumber")
    String getPackingNumber(@Param("packingNumber") String packingNumber);

    @Modifying
    @Query(value = "INSERT INTO packing_info(" +
            "date, packing_number, customer_id, old_sku, new_sku, operation_quantity, weight, carton_size, operation_id, is_weight_confirmed) " +
            "VALUES (:packingDate, :packingNumber, :customerId, :oldSku, :newSku, :quantity, :weight, :cartonSize, :operationNumber, :isWeightConfirmed)",
            nativeQuery = true)
    void insertPackingInfo(@Param("packingDate") String packingDate,
                           @Param("packingNumber") String packingNumber,
                           @Param("customerId") int customerId,
                           @Param("oldSku") String oldSku,
                           @Param("newSku") String newSku,
                           @Param("quantity") int quantity,
                           @Param("weight") double weight,
                           @Param("cartonSize") String cartonSize,
                           @Param("operationNumber") int operationNumber,
                           @Param("isWeightConfirmed") int isWeightConfirmed);

}
