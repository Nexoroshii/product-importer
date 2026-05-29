package com.example.productimporter.service.image.catalog.impl;

import com.example.productimporter.service.image.catalog.ExcelHyperlinkCatalog;
import com.example.productimporter.service.image.catalog.dto.HyperlinkFileStructure;
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

    /**
     * В твоем файле колонка T.
     */
    private static final int IMAGE_COLUMN = 19;

    /**
     * Пока захардкодим колонку с названием. Потом можем сделать autodetect.
     */
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

            HyperlinkFileStructure structure =
                detect(sheet);

            log.info("Detected product column: {}, image column: {}", productNameColumn,
                structure.imageColumn());

            for (Row row : sheet) {

                Cell productCell = row.getCell(productNameColumn);

                if (productCell == null) {
                    continue;
                }

                String productName = getCellValue(productCell);

                if (productName.isBlank()) {
                    continue;
                }

                Cell imageCell = row.getCell(structure.imageColumn());

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

    private HyperlinkFileStructure detect(Sheet sheet) {

        int nameColumn = detectProductColumn(sheet);
        int imageColumn = detectImageColumn(sheet);

        return new HyperlinkFileStructure(
            nameColumn,
            imageColumn
        );
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

    private int detectProductColumn(Sheet sheet) {

        Map<Integer, Integer> scores = new HashMap<>();

        int rowsToAnalyze =
            Math.min(sheet.getLastRowNum(), 200);

        for (int rowIndex = 0; rowIndex <= rowsToAnalyze; rowIndex++) {

            Row row = sheet.getRow(rowIndex);

            if (row == null) {
                continue;
            }
            for (Cell cell : row) {
                String value = getCellValue(cell);
                if (isProductName(value)) {
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
                    "Product column not found"
                )
            )
            .getKey();
    }

    private boolean isProductName(String value) {

        if (value == null || value.isBlank()) {
            return false;
        }
        if (value.startsWith("http")) {
            return false;
        }
        if (value.startsWith("=")) {
            return false;
        }
        return value.length() > 10;
    }
}

