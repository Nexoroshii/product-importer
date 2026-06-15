package com.example.productimporter.service.image.impl;

import com.example.productimporter.client.ImageClient;
import com.example.productimporter.service.image.ImageProcessor;
import com.example.productimporter.service.image.ImageService;
import com.example.productimporter.service.image.dto.ImageDto;
import com.example.productimporter.service.image.dto.UploadImageResponse;
import com.example.productimporter.service.image.enums.ImageVariantType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageProcessor imageProcessor;
    private final ImageClient imageClient;

    @Override
    public List<ImageDto> prepareImages(Path source) {

        UploadImageResponse originalUpload = imageClient.upload(source);

        Path bigCrop = imageProcessor.createBigCrop(source);
        UploadImageResponse bigUpload;
        try {
            bigUpload = imageClient.upload(bigCrop);
        } finally {
            tryDelete(bigCrop);
        }

        Path smallCrop = imageProcessor.createSmallCrop(source);
        UploadImageResponse smallUpload;
        try {
            smallUpload = imageClient.upload(smallCrop);
        } finally {
            tryDelete(smallCrop);
        }

        return List.of(
            map(
                originalUpload,
                ImageVariantType.ORIGINAL
            ),
            map(
                bigUpload,
                ImageVariantType.CROPPED_BIG
            ),
            map(
                smallUpload,
                ImageVariantType.CROPPED_SMALL
            )
        );
    }

    private void tryDelete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete temp file: {}", path);
        }
    }

    private ImageDto map(UploadImageResponse upload,
        ImageVariantType type) {

        return new ImageDto(
            upload.fileName(),
            upload.url(),
            type.name(),
            0,
            upload.fileType()
        );
    }
}
