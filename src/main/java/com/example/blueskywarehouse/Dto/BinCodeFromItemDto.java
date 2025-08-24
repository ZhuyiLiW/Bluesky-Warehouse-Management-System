package com.example.blueskywarehouse.Dto;

import java.io.Serializable;

public record BinCodeFromItemDto(
        String name,
        Double boxStock,
        Integer unitStock,
        Integer id

)implements Serializable {}