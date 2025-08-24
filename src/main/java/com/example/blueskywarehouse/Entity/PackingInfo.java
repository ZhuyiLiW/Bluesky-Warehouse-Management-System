package com.example.blueskywarehouse.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "packing_info")
public class PackingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "date")
    private String date;
    @Column(name = "packing_number")
    private String packingNumber;
    @Column(name = "customer_id")
    private String customerId;
    @Column(name = "old_sku")
    private String oldSku;
    @Column(name = "new_sku")
    private String newSku;
    @Column(name = "operation_quantity")
    private int operationQuantity;
    @Column(name = "weight")
    private double weight;
    @Column(name = "carton_size")
    private String carton_size;
    @Column(name = "operation_id")
    private int operationId;
    @Version
    private Integer version;

    public PackingInfo() {

    }

    public PackingInfo(int id, String date, String packingNumber, String customerId, String oldSku, String newSku, int operationQuantity, double weight, String carton_size, int operationId, Integer version) {
        this.id = id;
        this.date = date;
        this.packingNumber = packingNumber;
        this.customerId = customerId;
        this.oldSku = oldSku;
        this.newSku = newSku;
        this.operationQuantity = operationQuantity;
        this.weight = weight;
        this.carton_size = carton_size;
        this.operationId = operationId;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPackingNumber() {
        return packingNumber;
    }

    public void setPackingNumber(String packingNumber) {
        this.packingNumber = packingNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOldSku() {
        return oldSku;
    }

    public void setOldSku(String oldSku) {
        this.oldSku = oldSku;
    }

    public String getNewSku() {
        return newSku;
    }

    public void setNewSku(String newSku) {
        this.newSku = newSku;
    }

    public int getOperationQuantity() {
        return operationQuantity;
    }

    public void setOperationQuantity(int operationQuantity) {
        this.operationQuantity = operationQuantity;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getCarton_size() {
        return carton_size;
    }

    public void setCarton_size(String carton_size) {
        this.carton_size = carton_size;
    }

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
