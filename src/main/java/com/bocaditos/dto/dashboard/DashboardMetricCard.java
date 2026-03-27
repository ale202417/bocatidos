package com.bocaditos.dto.dashboard;

public record DashboardMetricCard(
        String label,
        String value,
        String trend,
        String trendDirection,
        String helperText
) {
}
