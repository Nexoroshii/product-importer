package com.example.productimporter.domain.image;

import java.nio.file.Path;

public record ResolvedImage(
    Path imagePath,
    ImageSource source
) {
}
