package com.example.productimporter.service.image.provider;

import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.domain.image.ImageSource;
import com.example.productimporter.domain.image.ResolvedImage;
import com.example.productimporter.service.image.ImageProvider;
import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(999)
public class PlaceholderImageProvider implements ImageProvider {

    @Value("${images.placeholder}")
    private String placeholderImage;

    @PostConstruct
    void init() {
        if (!Files.isRegularFile(Path.of(placeholderImage))) {
            throw new IllegalStateException(
                "Placeholder image not found: " + placeholderImage
            );
        }
    }

    @Override
    public Optional<ResolvedImage> find(ProductImportItem item) {

        return Optional.of(
            new ResolvedImage(
                Path.of(placeholderImage),
                ImageSource.PLACEHOLDER
            )
        );
    }
}
