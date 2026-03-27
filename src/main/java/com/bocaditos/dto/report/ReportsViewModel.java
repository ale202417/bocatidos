package com.bocaditos.dto.report;

import java.util.List;

public record ReportsViewModel(
        String salesTotal,
        String expenseTotal,
        String netOperatingPosition,
        List<ReportLineItem> partnerSummary,
        List<ReportLineItem> customerSummary,
        List<ReportLineItem> expenseByCategory,
        List<ReportLineItem> productionEfficiency
) {
}
