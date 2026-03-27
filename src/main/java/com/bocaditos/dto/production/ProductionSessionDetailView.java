package com.bocaditos.dto.production;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ProductionSessionDetailView(
        Long id,
        LocalDate productionDate,
        LocalTime startTime,
        LocalTime endTime,
        int unitsProduced,
        BigDecimal hourlyLaborRate,
        BigDecimal otherCost,
        BigDecimal laborHours,
        BigDecimal laborCost,
        BigDecimal unitsPerHour,
        BigDecimal costPerUnit,
        String notes,
        List<ProductionSessionWorkerView> workers
) {
}
