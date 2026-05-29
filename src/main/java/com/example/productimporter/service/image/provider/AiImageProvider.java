package com.example.productimporter.service.image.provider;

import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.domain.image.ResolvedImage;
import com.example.productimporter.service.image.ImageProvider;
import java.util.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class AiImageProvider implements ImageProvider {

    @Override
    public Optional<ResolvedImage> find(ProductImportItem item) {
        return Optional.empty();
    }
}
