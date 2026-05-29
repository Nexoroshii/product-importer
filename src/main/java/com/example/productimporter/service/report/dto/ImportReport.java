package com.example.productimporter.service.report.dto;

import java.util.List;

public record ImportReport(
    int total,
    int success,
    int failed,
    int excelImages,
    int localImages,
    int aiImages,
    int placeholderImages,
    List<ImportResult> results
) {
}
