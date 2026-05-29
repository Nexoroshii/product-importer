package com.example.productimporter.domain;

public record ProductImportItem(
    String name,
    String productNumber,
    String categoryType,
    Integer storeId,
    String hyperlinkImageUrl
) {
}
