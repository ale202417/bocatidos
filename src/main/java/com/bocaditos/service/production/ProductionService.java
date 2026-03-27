package com.bocaditos.service.production;

import com.bocaditos.domain.production.ProductionSession;
import com.bocaditos.domain.production.ProductionSessionWorker;
import com.bocaditos.domain.production.ProductionWorker;
import com.bocaditos.dto.production.ProductionFilter;
import com.bocaditos.dto.production.ProductionFormOptions;
import com.bocaditos.dto.production.ProductionSessionDetailView;
import com.bocaditos.dto.production.ProductionSessionForm;
import com.bocaditos.dto.production.ProductionSessionListItem;
import com.bocaditos.dto.production.ProductionSessionWorkerView;
import com.bocaditos.dto.production.ProductionSummaryView;
import com.bocaditos.dto.production.ProductionWorkerEntryForm;
import com.bocaditos.repository.ProductionSessionRepository;
import com.bocaditos.repository.ProductionWorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

@Service
@Transactional(readOnly = true)
public class ProductionService {

    private static final int DEFAULT_ROWS = 3;

    private final ProductionSessionRepository productionSessionRepository;
    private final ProductionWorkerRepository productionWorkerRepository;

    public ProductionService(
            ProductionSessionRepository productionSessionRepository,
            ProductionWorkerRepository productionWorkerRepository
    ) {
        this.productionSessionRepository = productionSessionRepository;
        this.productionWorkerRepository = productionWorkerRepository;
    }

    public List<ProductionSessionListItem> getSessions(ProductionFilter filter) {
        return productionSessionRepository.findForList(filter.getStartDate(), filter.getEndDate()).stream()
                .map(session -> {
                    BigDecimal laborHours = calculateLaborHours(session);
                    BigDecimal laborCost = calculateLaborCost(session);
                    BigDecimal unitsPerHour = laborHours.compareTo(BigDecimal.ZERO) > 0
                            ? BigDecimal.valueOf(session.getUnitsProduced()).divide(laborHours, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    return new ProductionSessionListItem(
                            session.getId(),
                            session.getProductionDate(),
                            session.getStartTime() + " - " + session.getEndTime(),
                            session.getUnitsProduced(),
                            laborHours,
                            unitsPerHour,
                            laborCost
                    );
                })
                .toList();
    }

    public ProductionSummaryView getSummary(ProductionFilter filter) {
        List<ProductionSession> sessions = productionSessionRepository.findForList(filter.getStartDate(), filter.getEndDate());
        int totalUnits = sessions.stream().mapToInt(ProductionSession::getUnitsProduced).sum();
        BigDecimal totalLaborHours = sessions.stream().map(this::calculateLaborHours).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgUnitsPerHour = totalLaborHours.compareTo(BigDecimal.ZERO) > 0
                ? BigDecimal.valueOf(totalUnits).divide(totalLaborHours, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal totalLaborCost = sessions.stream().map(this::calculateLaborCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ProductionSummaryView(totalUnits, totalLaborHours, avgUnitsPerHour, totalLaborCost);
    }

    public ProductionSessionDetailView getSession(Long id) {
        ProductionSession session = productionSessionRepository.findDetailedById(id)
                .orElseThrow(() -> new EntityNotFoundException("Production session not found"));
        BigDecimal laborHours = calculateLaborHours(session);
        BigDecimal laborCost = calculateLaborCost(session);
        BigDecimal unitsPerHour = laborHours.compareTo(BigDecimal.ZERO) > 0
                ? BigDecimal.valueOf(session.getUnitsProduced()).divide(laborHours, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal costPerUnit = session.getUnitsProduced() > 0
                ? laborCost.add(session.getOtherCost()).divide(BigDecimal.valueOf(session.getUnitsProduced()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new ProductionSessionDetailView(
                session.getId(),
                session.getProductionDate(),
                session.getStartTime(),
                session.getEndTime(),
                session.getUnitsProduced(),
                session.getHourlyLaborRate(),
                session.getOtherCost(),
                laborHours,
                laborCost,
                unitsPerHour,
                costPerUnit,
                session.getNotes(),
                session.getSessionWorkers().stream()
                        .map(worker -> new ProductionSessionWorkerView(
                                worker.getWorker().getFullName(),
                                worker.getHoursWorked(),
                                worker.getHourlyRate(),
                                worker.getLaborCost()
                        ))
                        .toList()
        );
    }

    public ProductionSessionForm emptyForm() {
        ProductionSessionForm form = new ProductionSessionForm();
        form.setWorkers(defaultWorkerRows());
        return form;
    }

    public ProductionFormOptions getFormOptions() {
        return new ProductionFormOptions(
                productionWorkerRepository.findAllByOrderByFullNameAsc().stream()
                        .map(worker -> new ProductionFormOptions.ReferenceOption(
                                worker.getId(),
                                worker.getFullName(),
                                worker.getDefaultHourlyRate().toPlainString()
                        ))
                        .toList()
        );
    }

    @Transactional
    public ProductionSession createSession(ProductionSessionForm form, BindingResult bindingResult) {
        if (form.getEndTime() != null && form.getStartTime() != null && !form.getEndTime().isAfter(form.getStartTime())) {
            bindingResult.rejectValue("endTime", "endTime.invalid", "End time must be after start time.");
            return null;
        }
        List<ProductionWorkerEntryForm> validWorkers = form.getWorkers().stream()
                .filter(worker -> worker.getWorkerId() != null && worker.getHoursWorked() != null && worker.getHourlyRate() != null)
                .toList();
        if (validWorkers.isEmpty()) {
            bindingResult.reject("workers.required", "Add at least one worker entry for the session.");
            return null;
        }

        ProductionSession session = new ProductionSession();
        session.setProductionDate(form.getProductionDate());
        session.setStartTime(form.getStartTime());
        session.setEndTime(form.getEndTime());
        session.setUnitsProduced(form.getUnitsProduced());
        session.setHourlyLaborRate(form.getHourlyLaborRate());
        session.setOtherCost(form.getOtherCost());
        session.setNotes(form.getNotes());

        for (ProductionWorkerEntryForm workerEntry : validWorkers) {
            ProductionWorker worker = productionWorkerRepository.findById(workerEntry.getWorkerId())
                    .orElseThrow(() -> new EntityNotFoundException("Production worker not found"));
            ProductionSessionWorker sessionWorker = new ProductionSessionWorker();
            sessionWorker.setSession(session);
            sessionWorker.setWorker(worker);
            sessionWorker.setHoursWorked(workerEntry.getHoursWorked());
            sessionWorker.setHourlyRate(workerEntry.getHourlyRate());
            sessionWorker.setLaborCost(workerEntry.getHoursWorked().multiply(workerEntry.getHourlyRate()).setScale(2, RoundingMode.HALF_UP));
            session.getSessionWorkers().add(sessionWorker);
        }

        return productionSessionRepository.save(session);
    }

    @Transactional
    public void deleteSession(Long id) {
        if (!productionSessionRepository.existsById(id)) {
            throw new EntityNotFoundException("Production session not found");
        }
        productionSessionRepository.deleteById(id);
    }

    private List<ProductionWorkerEntryForm> defaultWorkerRows() {
        List<ProductionWorkerEntryForm> rows = new ArrayList<>();
        for (int i = 0; i < DEFAULT_ROWS; i++) {
            rows.add(new ProductionWorkerEntryForm());
        }
        return rows;
    }

    private BigDecimal calculateLaborHours(ProductionSession session) {
        return session.getSessionWorkers().stream()
                .map(ProductionSessionWorker::getHoursWorked)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateLaborCost(ProductionSession session) {
        return session.getSessionWorkers().stream()
                .map(ProductionSessionWorker::getLaborCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
