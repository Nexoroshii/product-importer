package com.example.productimporter.client.impl;

import com.example.productimporter.auth.TokenStorage;
import com.example.productimporter.client.ImageClient;
import com.example.productimporter.service.image.dto.UploadImageResponse;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ImageClientImpl
    implements ImageClient {

    private final WebClient webClient;
    private final TokenStorage tokenStorage;

    @Override
    public UploadImageResponse upload(Path path) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part(
            "file",
            new FileSystemResource(path)
        );

        return webClient.post()
            .uri("/storage/images")
            .headers(headers ->
                headers.setBearerAuth(
                    tokenStorage.getAccessToken()
                )
            )
            .contentType(
                MediaType.MULTIPART_FORM_DATA
            )
            .body(
                BodyInserters.fromMultipartData(
                    builder.build()
                )
            )
            .retrieve()
            .bodyToMono(UploadImageResponse.class)
            .block();
    }
}
