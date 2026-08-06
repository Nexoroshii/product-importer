package com.example.productimporter.service.image.download.impl;

import com.example.productimporter.service.image.download.ImageDownloader;
import com.example.productimporter.service.image.download.StorageProperties;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HttpImageDownloader implements ImageDownloader {

    private final HttpClient imageHttpClient;
    private final StorageProperties storageProperties;

    @Override
    public Path download(String url) {

        if (isLocalFile(url)) {

            Path localFile = Path.of(url);

            if (!Files.exists(localFile)) {
                throw new IllegalStateException("Local image file not found: " + url);
            }

            return localFile;
        }

        try {

            HttpRequest request =
                HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<byte[]> response =
                imageHttpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofByteArray()
                );

            if (response.statusCode() != 200) {

                throw new IllegalStateException(
                    "Image download failed. Status: "
                        + response.statusCode()
                        + ", url: "
                        + url
                );
            }

            String extension = extractExtension(url);

            Path downloadDir = Path.of(storageProperties.downloadsDirectory(), "downloads");

            Files.createDirectories(downloadDir);

            Path file = downloadDir.resolve(UUID.randomUUID() + extension);

            Files.write(file, response.body());

            return file;

        } catch (Exception e) {

            throw new RuntimeException(
                "Failed to download image: " + url,
                e
            );
        }
    }

    private boolean isLocalFile(String reference) {

        String lower = reference.toLowerCase();

        return !lower.startsWith("http://") && !lower.startsWith("https://");
    }

    private String extractExtension(String url) {

        int dotIndex =
            url.lastIndexOf('.');

        if (dotIndex == -1) {
            return ".jpg";
        }

        String extension =
            url.substring(dotIndex);

        int queryIndex =
            extension.indexOf('?');

        if (queryIndex != -1) {
            extension =
                extension.substring(
                    0,
                    queryIndex
                );
        }

        return extension;
    }

}
