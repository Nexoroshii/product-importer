package com.example.productimporter.service.report.impl;

import com.example.productimporter.service.report.ReportExporter;
import com.example.productimporter.service.report.dto.ImportReport;
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
        log.info("Success: {}", report.success());
        log.info("Failed: {}", report.failed());

        log.info("Excel images: {}", report.excelImages());
        log.info("Local images: {}", report.localImages());
        log.info("AI images: {}", report.aiImages());
        log.info("Placeholder images: {}", report.placeholderImages());

        log.info("================================");
    }
}
