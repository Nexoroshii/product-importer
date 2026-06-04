package com.example.productimporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ProductImporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductImporterApplication.class, args);
    }

}
