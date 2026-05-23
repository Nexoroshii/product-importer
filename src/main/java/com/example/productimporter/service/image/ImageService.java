package com.example.productimporter.service.image;

import com.example.productimporter.service.image.dto.ImageDto;
import java.nio.file.Path;
import java.util.List;

public interface ImageService {

    List<ImageDto> prepareImages(Path source);
}
