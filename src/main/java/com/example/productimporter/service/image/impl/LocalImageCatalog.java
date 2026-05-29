package com.example.productimporter.service.image.impl;

import com.example.productimporter.service.image.ImageCatalog;
import com.example.productimporter.service.image.utils.FilenameNormalizer;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalImageCatalog implements ImageCatalog {

    private final FilenameNormalizer normalizer;

    @Value("${images.directory}")
    private String imagesDirectory;

    private Map<String, Path> cache;

    @PostConstruct
    void init() throws IOException {

        cache = Files.walk(Path.of(imagesDirectory))
            .filter(Files::isRegularFile)
            .collect(Collectors.toMap(
                file ->
                    normalizer.normalize(
                        file.getFileName()
                            .toString()
                    ),
                Function.identity(),
                (a, b) -> a
            ));
    }

    @Override
    public Map<String, Path> images() {
        return cache;
    }
}
