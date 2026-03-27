package com.bocaditos.dto.expense;

import com.bocaditos.domain.expense.ExpenseType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseDetailView(
        Long id,
        LocalDate expenseDate,
        String categoryName,
        String partnerName,
        ExpenseType type,
        BigDecimal amount,
        String description,
        String notes
) {
}
