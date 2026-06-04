package com.example.productimporter.source.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "import")
public record ImportProperties(
    String excelFile,
    String hyperlinksFile,
    ImportMode mode
) {

    public ImportMode modeOrDefault() {
        return mode == null ? ImportMode.FULL : mode;
    }
}
