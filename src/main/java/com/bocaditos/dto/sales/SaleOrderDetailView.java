package com.bocaditos.dto.sales;

import com.bocaditos.domain.sales.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SaleOrderDetailView(
        Long id,
        String orderNumber,
        LocalDate orderDate,
        String partnerName,
        String customerName,
        PaymentStatus paymentStatus,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        BigDecimal amountPaid,
        BigDecimal balanceDue,
        String notes,
        List<SaleOrderDetailItemView> items
) {
}
