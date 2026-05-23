package com.example.productimporter.client;

import com.example.productimporter.dto.CreateProductDefinitionRequest;

public interface ProductDefinitionClient {

    void create(CreateProductDefinitionRequest request);
}
