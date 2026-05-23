package com.example.productimporter.service.image.impl;

import com.example.productimporter.service.image.ImageProcessor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

@Service
public class ThumbnailatorImageProcessor implements ImageProcessor {

    @Override
    public Path createBigCrop(Path source) {

        try {

            Path temp = Files.createTempFile(
                "big-crop-",
                ".jpg"
            );

            Thumbnails.of(source.toFile())
                .size(1200, 1200)
                .toFile(temp.toFile());

            return temp;

        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to create big crop",
                e
            );
        }
    }

    @Override
    public Path createSmallCrop(Path source) {

        try {

            Path temp = Files.createTempFile(
                "small-crop-",
                ".jpg"
            );

            Thumbnails.of(source.toFile())
                .size(300, 300)
                .toFile(temp.toFile());

            return temp;

        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to create small crop",
                e
            );
        }
    }
}
