package com.bocaditos.dto.expense;

import java.math.BigDecimal;

public record ExpenseSummaryView(
        BigDecimal totalExpenses,
        long reimbursementCount,
        long businessExpenseCount
) {
}
