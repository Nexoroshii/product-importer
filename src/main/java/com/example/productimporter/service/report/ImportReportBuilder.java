package com.example.productimporter.service.report;

import com.example.productimporter.domain.image.ImageSource;
import com.example.productimporter.service.report.dto.ImportReport;
import com.example.productimporter.service.report.dto.ImportResult;
import com.example.productimporter.service.report.dto.ImportStatus;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ImportReportBuilder {

    public ImportReport build(List<ImportResult> results) {

        return new ImportReport(

            results.size(),

            (int) results.stream()
                .filter(r -> r.status() == ImportStatus.SUCCESS)
                .count(),

            (int) results.stream()
                .filter(r -> r.status() == ImportStatus.FAILED)
                .count(),

            count(results, ImageSource.EXCEL_HYPERLINK),

            count(results, ImageSource.LOCAL_FOLDER),

            count(results, ImageSource.AI_SEARCH),

            count(results, ImageSource.PLACEHOLDER),

            results
        );
    }

    private int count(List<ImportResult> results,
        ImageSource source) {

        return (int) results.stream()
            .filter(r -> r.imageSource() == source)
            .count();
    }
}
