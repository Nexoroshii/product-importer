package com.example.productimporter.source.excel;

import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.source.ProductImportSource;
import com.example.productimporter.source.config.ImportProperties;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExcelProductImportSource implements ProductImportSource {

    private static final Pattern PRODUCT_PATTERN = Pattern.compile(
        "Определение продукта (.+?) не найдено");

    private final ImportProperties properties;

    @Override
    public List<ProductImportItem> load() {

        List<ProductImportItem> result = new ArrayList<>();
        Set<String> seenProducts = new LinkedHashSet<>();

        try (
            Workbook workbook =
                WorkbookFactory.create(
                    Path.of(properties.excelFile())
                        .toFile()
                )
        ) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                Cell cell = row.getCell(0);

                if (cell == null) {
                    continue;
                }

                String message = cell.getStringCellValue();

                extractProductName(message)
                    .ifPresent(productName -> {
                        if (!seenProducts.add(productName.toLowerCase())) {
                            return;
                        }
                        result.add(
                            new ProductImportItem(
                                productName,
                                productName,
                                "LOCAL",
                                18,
                                null
                            )
                        );
                    });
            }

            return result;

        } catch (Exception e) {

            throw new RuntimeException(
                "Failed to read import file",
                e
            );
        }
    }

    private Optional<String> extractProductName(
        String message
    ) {

        Matcher matcher = PRODUCT_PATTERN.matcher(message);

        if (!matcher.find()) {
            return Optional.empty();
        }

        return Optional.of(
            matcher.group(1)
                .trim()
        );
    }
}
