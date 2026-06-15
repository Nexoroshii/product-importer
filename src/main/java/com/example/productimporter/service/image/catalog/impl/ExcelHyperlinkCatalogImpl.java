package com.example.productimporter.service.image.catalog.impl;

import com.example.productimporter.service.image.catalog.ExcelHyperlinkCatalog;
import com.example.productimporter.service.image.utils.FilenameNormalizer;
import com.example.productimporter.source.config.ImportProperties;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelHyperlinkCatalogImpl implements ExcelHyperlinkCatalog {

    @Value("${import.product-name-column}")
    private int productNameColumn;

    private final ImportProperties properties;
    private final FilenameNormalizer normalizer;

    private final Map<String, String> imageUrls = new HashMap<>();

    @PostConstruct
    public void init() {

        try (
            Workbook workbook =
                WorkbookFactory.create(
                    new File(
                        properties.hyperlinksFile()
                    )
                )) {
            Sheet sheet = workbook.getSheetAt(0);

            int imageColumn = detectImageColumn(sheet);

            log.info("Product name column (from config): {}, detected image column: {}",
                productNameColumn, imageColumn);

            for (Row row : sheet) {

                Cell productCell = row.getCell(productNameColumn);

                if (productCell == null) {
                    continue;
                }

                String productName = getCellValue(productCell);

                if (productName.isBlank()) {
                    continue;
                }

                Cell imageCell = row.getCell(imageColumn);

                if (imageCell == null) {
                    continue;
                }

                Hyperlink hyperlink = imageCell.getHyperlink();

                if (hyperlink == null) {
                    continue;
                }

                imageUrls.put(
                    normalizer.normalize(
                        productName
                    ),
                    hyperlink.getAddress()
                );
            }

            log.info(
                "Loaded {} image hyperlinks",
                imageUrls.size()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                "Failed to load hyperlink catalog",
                e
            );
        }
    }

    @Override
    public Optional<String> findImageUrl(String productName) {

        return Optional.ofNullable(
            imageUrls.get(
                normalizer.normalize(
                    productName
                )
            )
        );
    }

    private String getCellValue(Cell cell) {

        return switch (cell.getCellType()) {

            case STRING -> cell.getStringCellValue();

            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    yield "";
                }
            }

            default -> "";
        };
    }

    private int detectImageColumn(Sheet sheet) {

        Map<Integer, Integer> scores = new HashMap<>();
        int rowsToAnalyze = Math.min(sheet.getLastRowNum(), 200);
        for (int rowIndex = 0; rowIndex <= rowsToAnalyze; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }

            for (Cell cell : row) {
                if (cell.getHyperlink() != null) {
                    scores.merge(
                        cell.getColumnIndex(),
                        1,
                        Integer::sum
                    );
                }
            }
        }

        return scores.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .orElseThrow(() ->
                new IllegalStateException(
                    "Image column not found"
                )
            )
            .getKey();
    }

}

