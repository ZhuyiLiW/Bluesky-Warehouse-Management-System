package com.example.blueskywarehouse.Entity;

import jakarta.persistence.*;

@Entity
public class AllStock {


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
    @Version
    private Integer version;

    public AllStock() {
    }
    public AllStock(int id, String name, double totalBoxStock, double totalUnitStock) {
        this.id = id;
        this.name = name;
        this.totalBoxStock = totalBoxStock;
        this.totalUnitStock = totalUnitStock;
    }

    public AllStock(int id, String name, double totalBoxStock, double totalUnitStock, Integer version) {
        this.id = id;
        this.name = name;
        this.totalBoxStock = totalBoxStock;
        this.totalUnitStock = totalUnitStock;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
