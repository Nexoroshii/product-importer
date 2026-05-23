package com.example.productimporter.service.image;

import java.nio.file.Path;

public interface ImageProcessor {

    Path createBigCrop(Path source);

    Path createSmallCrop(Path source);
}
