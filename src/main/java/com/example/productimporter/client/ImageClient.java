package com.example.productimporter.client;

import com.example.productimporter.service.image.dto.UploadImageResponse;
import java.nio.file.Path;

public interface ImageClient {

    UploadImageResponse upload(Path path);
}
