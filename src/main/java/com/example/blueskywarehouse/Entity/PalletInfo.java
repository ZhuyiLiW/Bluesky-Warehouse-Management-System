package com.example.blueskywarehouse.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PalletInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "item_id")
    private int itemId;
    @Column(name = "pallet_length")
    private Double palletLength;
    @Column(name = "pallet_width")
    private Double palletWidth;
    @Column(name = "pallet_height")
    private Double palletHeight;
    @Column(name = "volume")
    private Double volume;
    @Column(name = "production_date")
    private LocalDateTime productionDate;
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    @Column(name = "storage_date")
    private LocalDateTime storageDate;
    @Column(name = "box_stock")
    private Double boxStock;
    @Column(name = "unit_stock")
    private int unitStock;
    @Column(name = "note")
    private String note;
    @Version
    private Integer version;

    public PalletInfo() {

    }

    public PalletInfo(int id, int itemId, Double palletLength, Double palletWidth, Double palletHeight, Double volume, LocalDateTime productionDate, LocalDateTime expirationDate, LocalDateTime storageDate, Double boxStock, int unitStock, String note, Integer version) {
        this.id = id;
        this.itemId = itemId;
        this.palletLength = palletLength;
        this.palletWidth = palletWidth;
        this.palletHeight = palletHeight;
        this.volume = volume;
        this.productionDate = productionDate;
        this.expirationDate = expirationDate;
        this.storageDate = storageDate;
        this.boxStock = boxStock;
        this.unitStock = unitStock;
        this.note = note;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Double getPalletLength() {
        return palletLength;
    }

    public void setPalletLength(Double palletLength) {
        this.palletLength = palletLength;
    }

    public Double getPalletWidth() {
        return palletWidth;
    }

    public void setPalletWidth(Double palletWidth) {
        this.palletWidth = palletWidth;
    }

    public Double getPalletHeight() {
        return palletHeight;
    }

    public void setPalletHeight(Double palletHeight) {
        this.palletHeight = palletHeight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public LocalDateTime getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDateTime productionDate) {
        this.productionDate = productionDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDateTime getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(LocalDateTime storageDate) {
        this.storageDate = storageDate;
    }

    public Double getBoxStock() {
        return boxStock;
    }

    public void setBoxStock(Double boxStock) {
        this.boxStock = boxStock;
    }

    public int getUnitStock() {
        return unitStock;
    }

    public void setUnitStock(int unitStock) {
        this.unitStock = unitStock;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
