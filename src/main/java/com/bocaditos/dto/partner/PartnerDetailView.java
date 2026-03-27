package com.bocaditos.dto.partner;

import com.bocaditos.domain.partner.PartnerStatus;
import java.math.BigDecimal;

public record PartnerDetailView(
        Long id,
        String fullName,
        String phone,
        String email,
        PartnerStatus status,
        String notes,
        long salesCount,
        BigDecimal salesTotal,
        BigDecimal expenseTotal,
        BigDecimal netContribution
) {
}
