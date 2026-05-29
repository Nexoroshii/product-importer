package com.example.productimporter.service.image.catalog;

import java.util.Optional;

public interface ExcelHyperlinkCatalog {

    Optional<String> findImageUrl(String productName);
}