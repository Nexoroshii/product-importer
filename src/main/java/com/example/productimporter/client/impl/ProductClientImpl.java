package com.example.productimporter.client.impl;

import com.example.productimporter.auth.TokenStorage;
import com.example.productimporter.client.ProductDefinitionClient;
import com.example.productimporter.dto.CreateProductDefinitionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ProductClientImpl implements ProductDefinitionClient {

    private final WebClient webClient;
    private final TokenStorage tokenStorage;

    @Override
    public void create(CreateProductDefinitionRequest request) {

        webClient.post()
            .uri("/product-definitions")
            .header(
                HttpHeaders.AUTHORIZATION,
                "Bearer " +
                    tokenStorage.getAccessToken()
            )
            .bodyValue(request)
            .retrieve()
            .toBodilessEntity()
            .block();
    }
}
