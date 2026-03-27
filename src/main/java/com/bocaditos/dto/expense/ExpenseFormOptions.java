package com.bocaditos.dto.expense;

import java.util.List;

public record ExpenseFormOptions(
        List<ReferenceOption> categories,
        List<ReferenceOption> partners
) {

    public record ReferenceOption(Long id, String label) {
    }
}
