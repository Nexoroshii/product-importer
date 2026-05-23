package com.example.productimporter.service;

import java.nio.file.Path;

public interface ImageService {

    List<ImageDto> prepareAndUpload(Path original);
}
