package com.example.blueskywarehouse.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private String type;
    @Column(name = "units_per_box")
    private int unitPerBox ;
    @Column(name = "product_group")
    private String productGroup;
    @Version
    private Integer version;


    public Item() {
    }

    public Item(int id, String name, String type, int unitPerBox, String productGroup) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.unitPerBox = unitPerBox;
        this.productGroup = productGroup;
    }

    public Item(int id, String name, String type, int unitPerBox, String productGroup, Integer version) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.unitPerBox = unitPerBox;
        this.productGroup = productGroup;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUnitPerBox() {
        return unitPerBox;
    }

    public void setUnitPerBox(int unitPerBox) {
        this.unitPerBox = unitPerBox;
    }

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
