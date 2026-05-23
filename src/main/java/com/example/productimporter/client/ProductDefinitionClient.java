package com.example.productimporter.client;

import com.example.productimporter.dto.CreateProductDefinitionRequest;

public interface ProductClient {

    void create(CreateProductDefinitionRequest request);
}
