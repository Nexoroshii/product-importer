package com.example.productimporter.service.report;

import com.example.productimporter.domain.image.ImageSource;
import com.example.productimporter.service.report.dto.ImportReport;
import com.example.productimporter.service.report.dto.ImportResult;
import com.example.productimporter.service.report.dto.ImportStatus;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ImportReportBuilder {

    public ImportReport build(List<ImportResult> results) {
        List<ImportResult> safeResults =
            results == null ? Collections.emptyList() : results;

        return new ImportReport(

            safeResults.size(),

            (int) safeResults.stream()
                .filter(r -> r.status() == ImportStatus.PLANNED)
                .count(),

            (int) safeResults.stream()
                .filter(r -> r.status() == ImportStatus.SUCCESS)
                .count(),

            (int) safeResults.stream()
                .filter(r -> r.status() == ImportStatus.FAILED)
                .count(),

            count(safeResults, ImageSource.EXCEL_HYPERLINK),

            count(safeResults, ImageSource.LOCAL_FOLDER),

            count(safeResults, ImageSource.AI_SEARCH),

            count(safeResults, ImageSource.PLACEHOLDER),

            safeResults
        );
    }

    private int count(List<ImportResult> results,
        ImageSource source) {

        return (int) results.stream()
            .filter(r -> r.imageSource() == source)
            .count();
    }
}
