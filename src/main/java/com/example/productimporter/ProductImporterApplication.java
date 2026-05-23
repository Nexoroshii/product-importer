package com.example.productimporter;

import com.example.productimporter.config.ApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApiProperties.class)
public class ProductImporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductImporterApplication.class, args);
    }

}
