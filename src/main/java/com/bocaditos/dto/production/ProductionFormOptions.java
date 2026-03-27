package com.bocaditos.dto.production;

import java.util.List;

public record ProductionFormOptions(List<ReferenceOption> workers) {

    public record ReferenceOption(Long id, String label, String defaultRate) {
    }
}
