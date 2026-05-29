package com.example.productimporter.service.image.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageMatcher {

    private final FilenameNormalizer normalizer;

    public boolean matches(String productName,
        String fileName) {

        return normalizer.normalize(productName)
            .equals(normalizer.normalize(fileName));
    }
}
