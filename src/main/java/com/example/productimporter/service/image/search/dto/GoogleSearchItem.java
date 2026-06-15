package com.example.productimporter.service.image.search.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleSearchItem(
    @JsonProperty("link") String link,
    @JsonProperty("title") String title,
    @JsonProperty("mime") String mime
) {}
