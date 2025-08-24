package com.example.blueskywarehouse.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "amazon_products")
public class AmazonProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "old_barcode")
    private String oldBarCode;
    @Column(name = "new_barcode")
    private String newBarCode;
    @Column(name = "product_name")
    private String productName;
    @Column(name = "shop_name")
    private String shopName;
    @Column(name = "customer_id")
    private int customerId;
    @Column(name = "operation_id")
    private int operationId;
    @Column(name = "weight")
    private double weight;
    @Column(name = "quality")
    private double quality;
    @Column(name = "if_mix")
    private double ifMix;
    @Column(name = "remark")
    private String remark;

    @Version
    private Integer version;
    public AmazonProduct() {

    }

    public AmazonProduct(Long id, String oldBarCode, String newBarCode, String productName, String remark) {
        this.id = id;
        this.oldBarCode = oldBarCode;
        this.newBarCode = newBarCode;
        this.productName = productName;
        this.remark = remark;
    }

    public AmazonProduct(Long id, String oldBarCode, String newBarCode, String productName, String remark, Integer version) {
        this.id = id;
        this.oldBarCode = oldBarCode;
        this.newBarCode = newBarCode;
        this.productName = productName;
        this.remark = remark;
        this.version = version;
    }

    public AmazonProduct(Long id, String oldBarCode, String newBarCode, String productName, String shopName, int customerId, int operationId, double weight, double quality, double ifMix, String remark, Integer version) {
        this.id = id;
        this.oldBarCode = oldBarCode;
        this.newBarCode = newBarCode;
        this.productName = productName;
        this.shopName = shopName;
        this.customerId = customerId;
        this.operationId = operationId;
        this.weight = weight;
        this.quality = quality;
        this.ifMix = ifMix;
        this.remark = remark;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOldBarCode() {
        return oldBarCode;
    }

    public void setOldBarCode(String oldBarCode) {
        this.oldBarCode = oldBarCode;
    }

    public String getNewBarCode() {
        return newBarCode;
    }

    public void setNewBarCode(String newBarCode) {
        this.newBarCode = newBarCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getQuality() {
        return quality;
    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

    public double getIfMix() {
        return ifMix;
    }

    public void setIfMix(double ifMix) {
        this.ifMix = ifMix;
    }
}
