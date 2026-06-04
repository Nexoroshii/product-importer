package com.example.productimporter.service.image;

import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.domain.image.ResolvedImage;

public interface ProductImageResolver {

    ResolvedImage resolve(ProductImportItem item);
}
