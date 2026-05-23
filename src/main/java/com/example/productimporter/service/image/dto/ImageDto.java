package com.example.productimporter.service.image.dto;

public record ImageDto(

    String name,
    String url,
    String sizeType,
    Integer order,
    String type
) {

}
