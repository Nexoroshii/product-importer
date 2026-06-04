package com.example.productimporter.service;

import com.example.productimporter.client.ProductDefinitionClient;
import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.domain.image.ImageSource;
import com.example.productimporter.domain.image.ResolvedImage;
import com.example.productimporter.dto.CreateProductDefinitionRequest;
import com.example.productimporter.service.image.ImageService;
import com.example.productimporter.service.image.ProductImageResolver;
import com.example.productimporter.service.image.dto.ImageDto;
import com.example.productimporter.service.report.dto.ImportResult;
import com.example.productimporter.service.report.dto.ImportStatus;
import java.nio.file.Files;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class ProductImportService {

    private static final String UNKNOWN_PRODUCT_NAME = "<unknown>";

    private final ProductImageResolver imageResolver;
    private final ImageService imageService;
    private final ProductDefinitionClient productDefinitionClient;

    public ImportResult importProduct(ProductImportItem item) {
        if (item == null) {
            return failed(
                UNKNOWN_PRODUCT_NAME,
                null,
                null,
                "Import item is null"
            );
        }

        String productName = productName(item);
        ResolvedImage resolvedImage = null;

        try {
            validate(item);

            resolvedImage = imageResolver.resolve(item);
            validateResolvedImage(resolvedImage);

            List<ImageDto> images =
                imageService.prepareImages(resolvedImage.imagePath());

            if (images == null || images.isEmpty()) {
                return failed(
                    productName,
                    resolvedImage,
                    "Image service returned no uploaded images"
                );
            }

            CreateProductDefinitionRequest request =
                new CreateProductDefinitionRequest(
                    item.name().trim(),
                    item.productNumber().trim(),
                    item.categoryType().trim(),
                    List.of(item.storeId()),
                    images
                );

            productDefinitionClient.create(request);

            return new ImportResult(
                productName,
                ImportStatus.SUCCESS,
                resolvedImage.source(),
                resolvedImage.imagePath().toString(),
                "Imported"
            );

        } catch (Exception e) {
            return failed(productName, resolvedImage, readableMessage(e));
        }
    }

    private void validate(ProductImportItem item) {
        if (isBlank(item.name())) {
            throw new IllegalArgumentException("Product name is empty");
        }
        if (isBlank(item.productNumber())) {
            throw new IllegalArgumentException(
                "Product number is empty for product '" + item.name() + "'"
            );
        }
        if (isBlank(item.categoryType())) {
            throw new IllegalArgumentException(
                "Category type is empty for product '" + item.name() + "'"
            );
        }
        if (item.storeId() == null) {
            throw new IllegalArgumentException(
                "Store id is empty for product '" + item.name() + "'"
            );
        }
    }

    private void validateResolvedImage(ResolvedImage resolvedImage) {
        if (resolvedImage == null) {
            throw new IllegalStateException("Image resolver returned no image");
        }
        if (resolvedImage.source() == null) {
            throw new IllegalStateException("Image resolver returned image with no source");
        }
        if (resolvedImage.imagePath() == null) {
            throw new IllegalStateException("Image resolver returned image with no path");
        }
        if (!Files.isRegularFile(resolvedImage.imagePath())) {
            throw new IllegalStateException(
                "Resolved image file does not exist: " + resolvedImage.imagePath()
            );
        }
    }

    private ImportResult failed(
        String productName,
        ResolvedImage resolvedImage,
        String message
    ) {
        return failed(
            productName,
            resolvedImage == null ? null : resolvedImage.source(),
            resolvedImage == null || resolvedImage.imagePath() == null
                ? null
                : resolvedImage.imagePath().toString(),
            message
        );
    }

    private ImportResult failed(
        String productName,
        ImageSource imageSource,
        String imageLocation,
        String message
    ) {
        return new ImportResult(
            productName,
            ImportStatus.FAILED,
            imageSource,
            imageLocation,
            message
        );
    }

    private String productName(ProductImportItem item) {
        if (item == null || isBlank(item.name())) {
            return UNKNOWN_PRODUCT_NAME;
        }

        return item.name().trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String readableMessage(Exception e) {
        if (e instanceof WebClientResponseException webException) {
            String body = webException.getResponseBodyAsString();
            if (!isBlank(body)) {
                return "API request failed with status "
                    + webException.getStatusCode()
                    + ": "
                    + body;
            }

            return "API request failed with status " + webException.getStatusCode();
        }

        String message = e.getMessage();
        if (!isBlank(message)) {
            return message;
        }

        return e.getClass().getSimpleName();
    }
}
