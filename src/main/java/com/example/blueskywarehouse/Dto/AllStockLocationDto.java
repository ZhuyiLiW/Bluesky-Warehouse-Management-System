package com.example.blueskywarehouse.Dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public record AllStockLocationDto    (
        String name,
        String location,
        Double totalBoxStock,
        Long totalUnitStock
)implements Serializable {}