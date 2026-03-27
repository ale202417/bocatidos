package com.bocaditos.dto.production;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductionSessionListItem(
        Long id,
        LocalDate productionDate,
        String sessionWindow,
        int unitsProduced,
        BigDecimal laborHours,
        BigDecimal unitsPerHour,
        BigDecimal laborCost
) {
}
