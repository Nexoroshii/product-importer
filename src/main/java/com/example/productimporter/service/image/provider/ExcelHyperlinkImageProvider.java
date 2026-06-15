package com.example.productimporter.service.image.provider;


import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.domain.image.ImageSource;
import com.example.productimporter.domain.image.ResolvedImage;
import com.example.productimporter.service.image.ImageProvider;
import com.example.productimporter.service.image.download.ImageDownloader;
import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class ExcelHyperlinkImageProvider implements ImageProvider {

    private final ImageDownloader imageDownloader;

    @Override
    public Optional<ResolvedImage> find(ProductImportItem item) {

        if (item.hyperlinkImageUrl() == null
            || item.hyperlinkImageUrl()
            .isBlank()) {

            return Optional.empty();
        }

        try {
            Path image =
                imageDownloader.download(
                    item.hyperlinkImageUrl()
                );

            return Optional.of(
                new ResolvedImage(
                    image,
                    ImageSource.EXCEL_HYPERLINK
                )
            );

        } catch (Exception e) {
            log.warn("Failed to download image from hyperlink for product '{}': {}", item.name(), e.getMessage());
            return Optional.empty();
        }
    }
}
