package com.example.productimporter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api")
public record ApiProperties(
    String baseUrl,
    String username,
    String password
) {}
