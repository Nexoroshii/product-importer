package com.example.productimporter.service.image;

import com.example.productimporter.domain.ProductImportItem;
import java.util.List;

public interface ProductImageUrlEnricher {

    List<ProductImportItem> enrich(List<ProductImportItem> items);
}
