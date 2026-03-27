package com.bocaditos.dto.customer;

import java.math.BigDecimal;

public record CustomerDetailView(
        Long id,
        String fullName,
        String phone,
        String email,
        String address,
        String notes,
        long orderCount,
        BigDecimal lifetimeSales
) {
}
