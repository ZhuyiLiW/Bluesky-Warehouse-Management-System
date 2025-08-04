package com.example.blueskywarehouse.Entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class PriceList implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "item_name")
    private String itemName;
    @Column(name="price")
    private String price;
    @Transient
    private LocalDate date;
    @Column(name="remark")
    private String remark;
    @Version
    private Integer version;

    private static final long serialVersionUID = 1L;

    public PriceList() {

    }

    public PriceList(int id, String itemName, String price,String remark) {
        this.id = id;
        this.itemName = itemName;
        this.price = price;
        this.remark=remark;
    }

    public PriceList(int id, String itemName, String price, LocalDate date) {
        this.id = id;
        this.itemName = itemName;
        this.price = price;
        this.date = date;
    }

    public PriceList(int id, String itemName, String price, LocalDate date, String remark, Integer version) {
        this.id = id;
        this.itemName = itemName;
        this.price = price;
        this.date = date;
        this.remark = remark;
        this.version = version;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
}
