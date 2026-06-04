package com.example.productimporter.service.report.impl;

import com.example.productimporter.domain.image.ImageSource;
import com.example.productimporter.service.report.ReportExporter;
import com.example.productimporter.service.report.dto.ImportReport;
import com.example.productimporter.service.report.dto.ImportStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsoleReportExporter implements ReportExporter {

    @Override
    public void export(ImportReport report) {
        log.info("================================");
        log.info("IMPORT REPORT");
        log.info("================================");

        log.info("Total: {}", report.total());
        log.info("Planned: {}", report.planned());
        log.info("Success: {}", report.success());
        log.info("Failed: {}", report.failed());

        log.info("Excel images: {}", report.excelImages());
        log.info("Local images: {}", report.localImages());
        log.info("AI images: {}", report.aiImages());
        log.info("Placeholder images: {}", report.placeholderImages());

        logPlannedRows(report);
        logFailedRows(report);
        logPlaceholderRows(report);

        log.info("================================");
    }

    private void logPlannedRows(ImportReport report) {
        if (report.planned() == 0) {
            return;
        }

        log.info("Planned image sources:");
        report.results()
            .stream()
            .filter(result -> result.status() == ImportStatus.PLANNED)
            .forEach(result ->
                log.info(
                    "{} | imageSource={} | imageLocation={}",
                    result.productName(),
                    valueOrDash(result.imageSource()),
                    valueOrDash(result.imageLocation())
                )
            );
    }

    private void logFailedRows(ImportReport report) {
        if (report.failed() == 0) {
            return;
        }

        log.warn("Failed products:");
        report.results()
            .stream()
            .filter(result -> result.status() == ImportStatus.FAILED)
            .forEach(result ->
                log.warn(
                    "{} | imageSource={} | imageLocation={} | error={}",
                    result.productName(),
                    valueOrDash(result.imageSource()),
                    valueOrDash(result.imageLocation()),
                    valueOrDash(result.message())
                )
            );
    }

    private void logPlaceholderRows(ImportReport report) {
        boolean hasSuccessfulPlaceholderRows = report.results()
            .stream()
            .anyMatch(result -> result.status() == ImportStatus.SUCCESS
                && result.imageSource() == ImageSource.PLACEHOLDER);

        if (!hasSuccessfulPlaceholderRows) {
            return;
        }

        log.warn("Products imported with placeholder image:");
        report.results()
            .stream()
            .filter(result -> result.status() == ImportStatus.SUCCESS)
            .filter(result -> result.imageSource() == ImageSource.PLACEHOLDER)
            .forEach(result -> log.warn("{}", result.productName()));
    }

    private String valueOrDash(Object value) {
        if (value == null) {
            return "-";
        }

        String text = value.toString();
        if (text.isBlank()) {
            return "-";
        }

        return text;
    }
}
