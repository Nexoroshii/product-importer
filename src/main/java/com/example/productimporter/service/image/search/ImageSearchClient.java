package com.example.productimporter.service.image.search;

import java.util.List;

public interface ImageSearchClient {
    List<ImageSearchResult> search(String query);
}
