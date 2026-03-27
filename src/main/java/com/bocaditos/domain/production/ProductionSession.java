package com.bocaditos.domain.production;

import com.bocaditos.domain.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "production_sessions")
public class ProductionSession extends BaseEntity {

    @Column(nullable = false)
    private LocalDate productionDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer unitsProduced;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyLaborRate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal otherCost = BigDecimal.ZERO;

    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionSessionWorker> sessionWorkers = new ArrayList<>();

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getUnitsProduced() {
        return unitsProduced;
    }

    public void setUnitsProduced(Integer unitsProduced) {
        this.unitsProduced = unitsProduced;
    }

    public BigDecimal getHourlyLaborRate() {
        return hourlyLaborRate;
    }

    public void setHourlyLaborRate(BigDecimal hourlyLaborRate) {
        this.hourlyLaborRate = hourlyLaborRate;
    }

    public BigDecimal getOtherCost() {
        return otherCost;
    }

    public void setOtherCost(BigDecimal otherCost) {
        this.otherCost = otherCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<ProductionSessionWorker> getSessionWorkers() {
        return sessionWorkers;
    }
}
