package com.example.blueskywarehouse.Dto;

import java.io.Serializable;

public record PriceListDto(
        Integer id,
        String itemName,
        String price,
        String remark
) implements Serializable {
}
