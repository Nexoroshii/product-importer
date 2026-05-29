package com.example.productimporter.service.image.download;

import java.nio.file.Path;

public interface ImageDownloader {

    Path download(String url);
}
