package com.example.productimporter.source;

import com.example.productimporter.domain.ProductImportItem;
import java.util.List;

public interface ProductImportSource {
    List<ProductImportItem> load();
}
