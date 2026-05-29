package com.example.productimporter.service.image.impl;

import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.service.image.ProductImageUrlEnricher;
import com.example.productimporter.service.image.catalog.ExcelHyperlinkCatalog;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImageUrlEnricherImpl implements ProductImageUrlEnricher {

    private final ExcelHyperlinkCatalog catalog;

    @Override
    public List<ProductImportItem> enrich(List<ProductImportItem> items) {

        return items.stream()
            .map(this::enrich)
            .toList();
    }

    private ProductImportItem enrich(ProductImportItem item) {

        String imageUrl = catalog.findImageUrl(item.name())
            .orElse(null);

        return new ProductImportItem(
            item.name(),
            item.productNumber(),
            item.categoryType(),
            item.storeId(),
            imageUrl
        );
    }
}
