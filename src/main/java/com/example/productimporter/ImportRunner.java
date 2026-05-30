package com.example.productimporter;

import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.domain.image.ResolvedImage;
import com.example.productimporter.service.AuthService;
import com.example.productimporter.service.ProductImportService;
import com.example.productimporter.service.image.ProductImageResolver;
import com.example.productimporter.service.image.ProductImageUrlEnricher;
import com.example.productimporter.source.ProductImportSource;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImportRunner implements CommandLineRunner {

    private final AuthService authService;
    private final ProductImportService productImportService;
    private final ProductImportSource importSource;
    private final ProductImageResolver imageResolver;
    private final ProductImageUrlEnricher hyperlinkEnricher;

    @Override
    public void run(String... args) {
// логинимся и получаем токен
        authService.login();
        // загружаем из файла список товаров, на которые будем создавать карточки
        List<ProductImportItem> items = importSource.load();

        System.out.println("Loaded products: " + items.size());

        List<ProductImportItem> enriched = hyperlinkEnricher.enrich(items);

        //
        enriched.stream()
//            .limit(5)
            .forEach(item -> {

                ResolvedImage image = imageResolver.resolve(item);

                System.out.println(
                    item.name()
                        + " -> "
                        + image.source()
                );
            });




        enriched.stream()
//            .limit(3)
            .forEach(productImportService::importProduct);

//        productImportService.importProduct(
//            "test1234",
//            "test1234",
//            "LOCAL",
//            18,
//            Path.of("images/test.jpg")
//        );

        System.out.println("Product imported successfully");
    }
}
