package com.example.productimporter.service.report;

import com.example.productimporter.service.report.dto.ImportReport;

public interface ReportExporter {

    void export(ImportReport report);
}
