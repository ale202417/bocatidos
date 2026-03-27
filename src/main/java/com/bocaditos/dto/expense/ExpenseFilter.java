package com.bocaditos.dto.expense;

import com.bocaditos.domain.expense.ExpenseType;
import java.time.LocalDate;

public class ExpenseFilter {

    private ExpenseType type;
    private Long categoryId;
    private LocalDate startDate;
    private LocalDate endDate;

    public ExpenseType getType() {
        return type;
    }

    public void setType(ExpenseType type) {
        this.type = type;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
