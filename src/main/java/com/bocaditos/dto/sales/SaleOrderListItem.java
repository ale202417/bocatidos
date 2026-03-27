package com.bocaditos.dto.sales;

import com.bocaditos.domain.sales.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SaleOrderListItem(
        Long id,
        String orderNumber,
        LocalDate orderDate,
        String partnerName,
        String customerName,
        int itemCount,
        PaymentStatus paymentStatus,
        BigDecimal totalAmount,
        BigDecimal amountPaid
) {
}
