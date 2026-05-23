package com.example.productimporter.dto;

import java.util.List;

public record CreateProductRequest(
    String name,
    String productNumber,
    String categoryType,
    List<Integer> storeIds,
    List<ImageRequest> images
) {}
