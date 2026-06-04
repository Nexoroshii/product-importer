package com.example.productimporter;

import com.example.productimporter.domain.ProductImportItem;
import com.example.productimporter.domain.image.ResolvedImage;
import com.example.productimporter.service.AuthService;
import com.example.productimporter.service.ProductImportService;
import com.example.productimporter.service.image.ProductImageResolver;
import com.example.productimporter.service.image.ProductImageUrlEnricher;
import com.example.productimporter.service.report.ImportReportBuilder;
import com.example.productimporter.service.report.ReportExporter;
import com.example.productimporter.service.report.dto.ImportReport;
import com.example.productimporter.service.report.dto.ImportResult;
import com.example.productimporter.service.report.dto.ImportStatus;
import com.example.productimporter.source.ProductImportSource;
import com.example.productimporter.source.config.ImportMode;
import com.example.productimporter.source.config.ImportProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
    name = "import.enabled",
    havingValue = "true",
    matchIfMissing = true
)
@RequiredArgsConstructor
public class ImportRunner implements CommandLineRunner {

    private final AuthService authService;
    private final ProductImportService productImportService;
    private final ProductImportSource importSource;
    private final ProductImageResolver imageResolver;
    private final ProductImageUrlEnricher hyperlinkEnricher;
    private final ImportReportBuilder reportBuilder;
    private final ReportExporter reportExporter;
    private final ImportProperties importProperties;

    @Override
    public void run(String... args) {
        ImportMode mode = importProperties.modeOrDefault();
        log.info("Starting product import in {} mode", mode);

        List<ProductImportItem> items = importSource.load();
        if (items == null) {
            log.warn("Import source returned null, treating it as empty list");
            items = List.of();
        }

        log.info("Loaded products: {}", items.size());

        if (items.isEmpty()) {
            log.warn("No products found in import source");
        }

        List<ProductImportItem> enriched = hyperlinkEnricher.enrich(items);

        List<ImportResult> results;
        if (mode == ImportMode.PREVIEW) {
            results = enriched.stream()
                .map(this::previewProduct)
                .toList();
        } else {
            authService.login();
            results = enriched.stream()
                .map(productImportService::importProduct)
                .toList();
        }

        ImportReport report = reportBuilder.build(results);
        reportExporter.export(report);
    }

    private ImportResult previewProduct(ProductImportItem item) {
        String productName = item == null || item.name() == null || item.name().isBlank()
            ? "<unknown>"
            : item.name().trim();

        try {
            if (item == null) {
                throw new IllegalArgumentException("Import item is null");
            }

            ResolvedImage image = imageResolver.resolve(item);

            return new ImportResult(
                productName,
                ImportStatus.PLANNED,
                image.source(),
                image.imagePath() == null ? null : image.imagePath().toString(),
                "Image planned"
            );
        } catch (Exception e) {
            return new ImportResult(
                productName,
                ImportStatus.FAILED,
                null,
                null,
                e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()
            );
        }
    }
}
