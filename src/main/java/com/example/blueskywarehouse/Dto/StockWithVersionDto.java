package com.example.blueskywarehouse.Dto;

import java.io.Serializable;

public record StockWithVersionDto(
        Long stock,
        Long version) implements Serializable {
}
