package com.example.productimporter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai.image-search")
public record AiSearchProperties(
    boolean enabled,
    String apiKey,
    String cx,
    int maxCandidates
) {}
