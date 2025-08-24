package com.example.blueskywarehouse.Dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record ItemCountDto(
        Double boxStock,
        Long unitStock
) implements Serializable {}
