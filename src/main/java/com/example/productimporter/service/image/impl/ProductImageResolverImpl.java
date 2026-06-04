package com.example.productimporter.service.image.impl;

import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.domain.image.ResolvedImage;
import com.example.productimporter.service.image.ImageProvider;
import com.example.productimporter.service.image.ProductImageResolver;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImageResolverImpl implements ProductImageResolver {

    private final List<ImageProvider> providers;

    @Override
    public ResolvedImage resolve(ProductImportItem item) {

        return providers.stream()
            .map(provider -> provider.find(item))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElseThrow(() ->
                new IllegalStateException(
                    "No image provider found"
                )
            );
    }
}
