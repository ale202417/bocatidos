package com.bocaditos.dto.sales;

import java.util.List;

public record SalesFormOptions(
        List<ReferenceOption> partners,
        List<ReferenceOption> customers
) {

    public record ReferenceOption(Long id, String label) {
    }
}
