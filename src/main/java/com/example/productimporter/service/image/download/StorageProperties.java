package com.example.productimporter.service.image.download;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage")
public record StorageProperties(
    String downloadsDirectory
) {
}
