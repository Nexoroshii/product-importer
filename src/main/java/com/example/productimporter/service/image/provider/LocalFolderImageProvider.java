package com.example.productimporter.service.image.provider;

import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.domain.image.ImageSource;
import com.example.productimporter.domain.image.ResolvedImage;
import com.example.productimporter.service.image.ImageCatalog;
import com.example.productimporter.service.image.ImageProvider;
import com.example.productimporter.service.image.utils.FilenameNormalizer;
import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
public class LocalFolderImageProvider implements ImageProvider {

    private final FilenameNormalizer normalizer;
    private final ImageCatalog imageCatalog;

    @Override
    public Optional<ResolvedImage> find(ProductImportItem item) {

        String normalizedName = normalizer.normalize(item.name());

        Path image = imageCatalog.images()
            .get(normalizedName);

        if (image == null) {
            return Optional.empty();
        }

        return Optional.of(
            new ResolvedImage(image, ImageSource.LOCAL_FOLDER)
        );
    }
}
