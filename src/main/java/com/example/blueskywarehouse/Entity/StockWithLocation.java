package com.example.blueskywarehouse.Entity;

import jakarta.persistence.*;

@Entity
public class StockWithLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "total_box_stock")
    private double totalBoxStock;

    @Column(name = "total_unit_stock")
    private double totalUnitStock;

    @Column(name = "location")
    private String location;
    @Version
    private Integer version;




    public StockWithLocation() {
    }


    public StockWithLocation(String name, String location, double totalBoxStock, double totalUnitStock) {
        this.name = name;
        this.location = location;
        this.totalBoxStock = totalBoxStock;
        this.totalUnitStock = totalUnitStock;
    }

    public StockWithLocation(int id, String name, double totalBoxStock, double totalUnitStock, String location, Integer version) {
        this.id = id;
        this.name = name;
        this.totalBoxStock = totalBoxStock;
        this.totalUnitStock = totalUnitStock;
        this.location = location;
        this.version = version;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalBoxStock() {
        return totalBoxStock;
    }

    public void setTotalBoxStock(double totalBoxStock) {
        this.totalBoxStock = totalBoxStock;
    }

    public double getTotalUnitStock() {
        return totalUnitStock;
    }

    public void setTotalUnitStock(double totalUnitStock) {
        this.totalUnitStock = totalUnitStock;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
