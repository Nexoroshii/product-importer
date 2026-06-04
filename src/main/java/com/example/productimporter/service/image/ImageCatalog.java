package com.example.productimporter.service.image;

import java.nio.file.Path;
import java.util.Map;

public interface ImageCatalog {

    Map<String, Path> images();
}
