package com.example.productimporter.service;

import com.example.productimporter.client.ProductDefinitionClient;
import com.example.productimporter.dto.CreateProductDefinitionRequest;
import com.example.productimporter.service.image.ImageService;
import com.example.productimporter.service.image.dto.ImageDto;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImportService {

    private final ImageService imageService;
    private final ProductDefinitionClient productDefinitionClient;

    public void importProduct(
        String name,
        String productNumber,
        String categoryType,
        Integer storeId,
        Path imagePath
    ) {

        List<ImageDto> images = imageService.prepareImages(imagePath);

        CreateProductDefinitionRequest request =
            new CreateProductDefinitionRequest(
                name,
                productNumber,
                categoryType,
                List.of(storeId),
                images
            );

        productDefinitionClient.create(request);
    }
}
