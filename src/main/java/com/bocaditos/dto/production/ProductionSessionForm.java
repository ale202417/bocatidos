package com.bocaditos.dto.production;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ProductionSessionForm {

    @NotNull
    private LocalDate productionDate = LocalDate.now();

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private Integer unitsProduced;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal hourlyLaborRate = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal otherCost = BigDecimal.ZERO;

    @Size(max = 500)
    private String notes;

    @Valid
    private List<ProductionWorkerEntryForm> workers = new ArrayList<>();

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

    public List<ProductionWorkerEntryForm> getWorkers() {
        return workers;
    }

    public void setWorkers(List<ProductionWorkerEntryForm> workers) {
        this.workers = workers;
    }
}
