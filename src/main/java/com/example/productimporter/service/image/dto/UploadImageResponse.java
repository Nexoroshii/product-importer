package com.example.productimporter.service.image.dto;

public record UploadImageResponse(

    String fileName,
    String url,
    String fileType
) {
}
