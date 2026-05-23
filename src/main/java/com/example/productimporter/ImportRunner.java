package com.example.productimporter;

import com.example.productimporter.service.AuthService;
import com.example.productimporter.service.ProductImportService;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImportRunner implements CommandLineRunner {

    private final AuthService authService;
    private final ProductImportService productImportService;

    @Override
    public void run(String... args) {

        authService.login();

        productImportService.importProduct(
            "test1234",
            "test1234",
            "LOCAL",
            18,
            Path.of("images/test.jpg")
        );

        System.out.println("Product imported successfully");
    }
}
