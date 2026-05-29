package com.example.productimporter.service;

import com.example.productimporter.client.ProductDefinitionClient;
import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.domain.image.ResolvedImage;
import com.example.productimporter.dto.CreateProductDefinitionRequest;
import com.example.productimporter.service.image.ImageService;
import com.example.productimporter.service.image.ProductImageResolver;
import com.example.productimporter.service.image.dto.ImageDto;
import com.example.productimporter.service.report.dto.ImportResult;
import com.example.productimporter.service.report.dto.ImportStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImportService {

    private final ProductImageResolver imageResolver;
    private final ImageService imageService;
    private final ProductDefinitionClient productDefinitionClient;

    public ImportResult importProduct(ProductImportItem item) {

        ResolvedImage resolvedImage = imageResolver.resolve(item);
        try {

            List<ImageDto> images =
                imageService.prepareImages(
                    resolvedImage.imagePath()
                );

            CreateProductDefinitionRequest request =
                new CreateProductDefinitionRequest(
                    item.name(),
                    item.productNumber(),
                    item.categoryType(),
                    List.of(item.storeId()),
                    images
                );

            productDefinitionClient.create(request);

            return new ImportResult(
                item.name(),
                ImportStatus.SUCCESS,
                resolvedImage.source(),
                resolvedImage.imagePath()
                    .toString(),
                null
            );

        } catch (Exception e) {

            return new ImportResult(
                item.name(),
                ImportStatus.FAILED,
                resolvedImage.source(),
                resolvedImage.imagePath()
                    .toString(),
                e.getMessage()
            );
        }
    }
}
