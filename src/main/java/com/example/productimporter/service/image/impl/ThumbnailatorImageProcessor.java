package com.example.productimporter.service.image.impl;

import com.example.productimporter.service.image.ImageProcessor;
import com.example.productimporter.service.image.download.StorageProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThumbnailatorImageProcessor implements ImageProcessor {

    private final StorageProperties storageProperties;

    @Override
    public Path createBigCrop(Path source) {

        try {

            Path cropFile = createCropFile("big-crop-");

            Thumbnails.of(source.toFile())
                .size(1200, 1200)
                .toFile(cropFile.toFile());

            return cropFile;

        } catch (IOException e) {
            throw new RuntimeException("Failed to create big crop", e);
        }
    }

    @Override
    public Path createSmallCrop(Path source) {

        try {
            Path cropFile = createCropFile("small-crop-");

            Thumbnails.of(source.toFile())
                .size(300, 300)
                .toFile(cropFile.toFile());

            return cropFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create small crop", e);
        }
    }

    private Path createCropFile(String prefix) throws IOException {

        Path cropDir = Path.of(storageProperties.downloadsDirectory(), "crops");

        Files.createDirectories(cropDir);

        return cropDir.resolve(prefix + UUID.randomUUID() + ".jpg");
    }
}
