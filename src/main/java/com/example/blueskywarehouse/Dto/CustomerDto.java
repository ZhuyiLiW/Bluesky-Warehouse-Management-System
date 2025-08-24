package com.example.blueskywarehouse.Dto;

import lombok.Data;

import java.sql.Date;

@Data
public class CustomerDto {
    private Long workNumber;
    private String deliveryLocation;
    private String productName;
    private Integer quantity;
    private String remark;
    private Date date;

    public CustomerDto(Long workNumber, String deliveryLocation, String productName, Integer quantity, String remark, Date date) {
        this.workNumber = workNumber;
        this.deliveryLocation = deliveryLocation;
        this.productName = productName;
        this.quantity = quantity;
        this.remark = remark;
        this.date = date;
    }

    public CustomerDto() {

    }
}
