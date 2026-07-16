package com.example.productimporter.service.image.search.impl;

import com.example.productimporter.config.AiSearchProperties;
import com.example.productimporter.service.image.search.ImageSearchClient;
import com.example.productimporter.service.image.search.ImageSearchResult;
import com.example.productimporter.service.image.search.dto.GoogleSearchResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleImageSearchClient implements ImageSearchClient {

    private static final String BASE_URL = "https://www.googleapis.com/customsearch/v1";

    private final AiSearchProperties properties;

    private final RestClient restClient = RestClient.builder()
        .baseUrl(BASE_URL)
        .build();

    @Override
    public List<ImageSearchResult> search(String query) {
        try {
            GoogleSearchResponse response = restClient.get()
                .uri(builder -> builder
                    .queryParam("key", properties.apiKey())
                    .queryParam("cx", properties.cx())
                    .queryParam("q", query)
                    .queryParam("searchType", "image")
                    .queryParam("num", properties.maxCandidates())
                    .queryParam("safe", "active")
                    .build()
                )
                .retrieve()
                .body(GoogleSearchResponse.class);

            if (response == null || response.items() == null) {
                log.debug("Google image search returned no results for '{}'", query);
                return List.of();
            }

            return response.items().stream()
                .map(item -> new ImageSearchResult(item.link(), item.title()))
                .toList();

        } catch (RestClientException e) {
            log.warn("Google image search failed for '{}': {}", query, e.getMessage());
            return List.of();
        }
    }
}
