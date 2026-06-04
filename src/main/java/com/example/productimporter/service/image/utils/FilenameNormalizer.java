package com.example.productimporter.service.image.utils;

import org.springframework.stereotype.Component;

@Component
public class FilenameNormalizer {

    public String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value
            .toLowerCase()
            .replace(".jpg", "")
            .replace(".jpeg", "")
            .replace(".png", "")
            .replace('_', ' ')
            .replace('-', ' ')
            .replaceAll("\\s+", " ")
            .trim();
    }
}
