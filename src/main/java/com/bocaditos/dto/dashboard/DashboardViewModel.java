package com.bocaditos.dto.dashboard;

import java.util.List;

public record DashboardViewModel(
        List<DashboardMetricCard> metricCards,
        List<String> salesTrendLabels,
        List<Integer> salesTrendValues,
        List<String> expenseBreakdownLabels,
        List<Integer> expenseBreakdownValues
) {
}
