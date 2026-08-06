package com.example.productimporter.service.image.utils;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class FilenameNormalizer {

    private static final Pattern IMAGE_EXTENSION = Pattern.compile(
        "\\.(jpe?g|png|gif|bmp|webp|tiff?|heic|heif|avif|jfif)$"
    );

    public String normalize(String value) {
        if (value == null) {
            return "";
        }

        return IMAGE_EXTENSION.matcher(value.toLowerCase())
            .replaceFirst("")
            .replace('_', ' ')
            .replace('-', ' ')
            .replaceAll("\\s+", " ")
            .trim();
    }
}
