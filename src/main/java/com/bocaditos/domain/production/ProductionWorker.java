package com.bocaditos.domain.production;

import com.bocaditos.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "production_workers")
public class ProductionWorker extends BaseEntity {

    @Column(nullable = false, length = 140)
    private String fullName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal defaultHourlyRate;

    @Column(nullable = false)
    private boolean active = true;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public BigDecimal getDefaultHourlyRate() {
        return defaultHourlyRate;
    }

    public void setDefaultHourlyRate(BigDecimal defaultHourlyRate) {
        this.defaultHourlyRate = defaultHourlyRate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
