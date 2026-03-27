package com.bocaditos.dto.sales;

import java.math.BigDecimal;

public record SaleOrderDetailItemView(
        String itemName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        String notes
) {
}
