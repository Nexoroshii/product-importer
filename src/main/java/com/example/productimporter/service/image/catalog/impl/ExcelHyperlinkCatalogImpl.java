package com.example.productimporter.service.image.catalog.impl;

import com.example.productimporter.service.image.catalog.ExcelHyperlinkCatalog;
import com.example.productimporter.service.image.download.StorageProperties;
import com.example.productimporter.service.image.utils.FilenameNormalizer;
import com.example.productimporter.source.config.ImportProperties;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Shape;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelHyperlinkCatalogImpl implements ExcelHyperlinkCatalog {

    private static final Pattern CYRILLIC = Pattern.compile("[а-яА-ЯёЁ]");

    private final ImportProperties properties;
    private final FilenameNormalizer normalizer;
    private final StorageProperties storageProperties;

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
            int productNameColumn = detectProductNameColumn(sheet, imageColumn);

            log.info("Detected product name column: {}, detected image column: {}",
                productNameColumn, imageColumn);

            Map<Integer, Picture> picturesByRow = mapPicturesByRow(sheet, imageColumn);

            for (Row row : sheet) {

                Cell productCell = row.getCell(productNameColumn);

                if (productCell == null) {
                    continue;
                }

                String productName = getCellValue(productCell);

                if (productName.isBlank()) {
                    continue;
                }

                String imageReference =
                    resolveImageReference(row, imageColumn, picturesByRow);

                if (imageReference == null) {
                    continue;
                }

                imageUrls.put(
                    normalizer.normalize(
                        productName
                    ),
                    imageReference
                );
            }

            log.info(
                "Loaded {} image references",
                imageUrls.size()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                "Failed to load hyperlink catalog",
                e
            );
        }
    }

    private String resolveImageReference(
        Row row,
        int imageColumn,
        Map<Integer, Picture> picturesByRow
    ) {

        Cell imageCell = row.getCell(imageColumn);

        if (imageCell != null) {
            Hyperlink hyperlink = imageCell.getHyperlink();
            if (hyperlink != null) {
                return hyperlink.getAddress();
            }
        }

        Picture picture = picturesByRow.get(row.getRowNum());

        if (picture == null) {
            return null;
        }

        return extractEmbeddedPicture(picture);
    }

    private String extractEmbeddedPicture(Picture picture) {

        try {
            PictureData data = picture.getPictureData();

            Path targetDir =
                Path.of(storageProperties.downloadsDirectory(), "embedded-images");

            Files.createDirectories(targetDir);

            String extension = data.suggestFileExtension();

            Path target = targetDir.resolve(
                UUID.randomUUID()
                    + (extension == null || extension.isBlank() ? ".jpg" : "." + extension)
            );

            Files.write(target, data.getData());

            return target.toAbsolutePath().toString();

        } catch (Exception e) {
            log.warn("Failed to extract embedded picture: {}", e.getMessage());
            return null;
        }
    }

    private Map<Integer, Picture> mapPicturesByRow(Sheet sheet, int imageColumn) {

        Map<Integer, Picture> result = new HashMap<>();

        Drawing<?> drawing = sheet.getDrawingPatriarch();

        if (drawing == null) {
            return result;
        }

        for (Shape shape : drawing) {

            if (!(shape instanceof Picture picture)) {
                continue;
            }

            if (picture.getClientAnchor().getCol1() != imageColumn) {
                continue;
            }

            result.put(picture.getClientAnchor().getRow1(), picture);
        }

        return result;
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

        Drawing<?> drawing = sheet.getDrawingPatriarch();

        if (drawing != null) {
            for (Shape shape : drawing) {
                if (shape instanceof Picture picture) {
                    scores.merge(
                        (int) picture.getClientAnchor().getCol1(),
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

    private int detectProductNameColumn(Sheet sheet, int imageColumn) {

        Map<Integer, Integer> scores = new HashMap<>();
        int rowsToAnalyze = Math.min(sheet.getLastRowNum(), 200);

        for (int rowIndex = 0; rowIndex <= rowsToAnalyze; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }

            for (Cell cell : row) {

                if (cell.getColumnIndex() == imageColumn) {
                    continue;
                }

                String value = getCellValue(cell);

                if (!value.isBlank() && CYRILLIC.matcher(value).find()) {
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
                    "Product name column not found"
                )
            )
            .getKey();
    }

}

