package com.bocaditos.dto.partner;

import com.bocaditos.domain.partner.PartnerStatus;
import java.math.BigDecimal;

public record PartnerListItem(
        Long id,
        String fullName,
        String phone,
        String email,
        PartnerStatus status,
        long salesCount,
        BigDecimal salesTotal,
        BigDecimal expenseTotal
) {
}
