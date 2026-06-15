package com.example.productimporter.service.image.provider;

import com.example.productimporter.config.AiSearchProperties;
import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.domain.image.ImageSource;
import com.example.productimporter.domain.image.ResolvedImage;
import com.example.productimporter.service.image.ImageProvider;
import com.example.productimporter.service.image.download.ImageDownloader;
import com.example.productimporter.service.image.search.ImageSearchClient;
import com.example.productimporter.service.image.search.ImageSearchResult;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(3)
@RequiredArgsConstructor
public class AiImageProvider implements ImageProvider {

    private final AiSearchProperties properties;
    private final ImageSearchClient searchClient;
    private final ImageDownloader imageDownloader;

    @Override
    public Optional<ResolvedImage> find(ProductImportItem item) {
        if (!properties.enabled()) {
            return Optional.empty();
        }

        String query = item.name();
        log.debug("Searching image for product '{}' via Google Image Search", query);

        List<ImageSearchResult> candidates = searchClient.search(query);

        if (candidates.isEmpty()) {
            log.debug("No image search results for '{}'", query);
            return Optional.empty();
        }

        for (ImageSearchResult candidate : candidates) {
            Optional<ResolvedImage> image = tryDownload(candidate, query);
            if (image.isPresent()) {
                return image;
            }
        }

        log.warn("All {} image candidates failed to download for '{}'", candidates.size(), query);
        return Optional.empty();
    }

    private Optional<ResolvedImage> tryDownload(ImageSearchResult candidate, String productName) {
        try {
            Path image = imageDownloader.download(candidate.imageUrl());
            log.info("Downloaded AI image for '{}' from '{}'", productName, candidate.imageUrl());
            return Optional.of(new ResolvedImage(image, ImageSource.AI_SEARCH));
        } catch (Exception e) {
            log.debug("Failed to download candidate '{}': {}", candidate.imageUrl(), e.getMessage());
            return Optional.empty();
        }
    }
}
