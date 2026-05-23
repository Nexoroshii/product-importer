package com.example.productimporter.service.image.impl;

import com.example.productimporter.client.ImageClient;
import com.example.productimporter.service.image.ImageProcessor;
import com.example.productimporter.service.image.ImageService;
import com.example.productimporter.service.image.dto.ImageDto;
import com.example.productimporter.service.image.dto.UploadImageResponse;
import com.example.productimporter.service.image.enums.ImageVariantType;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageProcessor imageProcessor;
    private final ImageClient imageClient;

    @Override
    public List<ImageDto> prepareImages(Path source) {

        UploadImageResponse originalUpload = imageClient.upload(source);

        Path bigCrop = imageProcessor.createBigCrop(source);

        UploadImageResponse bigUpload = imageClient.upload(bigCrop);

        Path smallCrop = imageProcessor.createSmallCrop(source);

        UploadImageResponse smallUpload = imageClient.upload(smallCrop);

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
