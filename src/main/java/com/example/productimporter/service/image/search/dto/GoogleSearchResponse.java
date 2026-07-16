package com.example.productimporter.service.image.search.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleSearchResponse(
    @JsonProperty("items") List<GoogleSearchItem> items
) {}
