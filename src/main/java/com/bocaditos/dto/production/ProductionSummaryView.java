package com.bocaditos.dto.production;

import java.math.BigDecimal;

public record ProductionSummaryView(
        int totalUnits,
        BigDecimal totalLaborHours,
        BigDecimal avgUnitsPerHour,
        BigDecimal totalLaborCost
) {
}
