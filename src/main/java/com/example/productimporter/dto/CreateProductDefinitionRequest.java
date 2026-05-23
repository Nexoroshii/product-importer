package com.example.productimporter.dto;

import com.example.productimporter.service.image.dto.ImageDto;
import java.util.List;

public record CreateProductDefinitionRequest(
    String name,
    String productNumber,
    String categoryType,
    List<Integer> storeIds,
    List<ImageDto> images
) {}
