package com.example.blueskywarehouse.Dto;

import java.io.Serializable;

public record AllItemFromBinDto(
        Integer id,
        String name,
        Double boxStock,
        Integer unitStock
) implements Serializable {
}
