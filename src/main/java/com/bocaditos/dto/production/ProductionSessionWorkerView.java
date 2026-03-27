package com.bocaditos.dto.production;

import java.math.BigDecimal;

public record ProductionSessionWorkerView(
        String workerName,
        BigDecimal hoursWorked,
        BigDecimal hourlyRate,
        BigDecimal laborCost
) {
}
