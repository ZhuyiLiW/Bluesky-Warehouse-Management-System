package com.example.blueskywarehouse.Dto;

import java.io.Serializable;

public record AllStockDto(
        Integer id,
        String name,
        Double totalBoxStock,
        Long totalUnitStock
) implements Serializable {
}
