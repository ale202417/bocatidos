package com.bocaditos.dto.customer;

import java.math.BigDecimal;

public record CustomerListItem(
        Long id,
        String fullName,
        String phone,
        String email,
        long orderCount,
        BigDecimal lifetimeSales
) {
}
