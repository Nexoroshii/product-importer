package com.example.productimporter.service.image;

import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.domain.image.ResolvedImage;
import java.util.Optional;

public interface ImageProvider {

    Optional<ResolvedImage> find(ProductImportItem item);
}
