package com.example.productimporter.service.report.dto;

import com.example.productimporter.domain.image.ImageSource;

public record ImportResult(
    String productName,
    ImportStatus status,
    ImageSource imageSource,
    String imageLocation,
    String message
) {
}
