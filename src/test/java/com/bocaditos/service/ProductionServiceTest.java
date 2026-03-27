package com.bocaditos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.bocaditos.domain.production.ProductionWorker;
import com.bocaditos.dto.production.ProductionSessionForm;
import com.bocaditos.dto.production.ProductionWorkerEntryForm;
import com.bocaditos.repository.ProductionSessionRepository;
import com.bocaditos.repository.ProductionWorkerRepository;
import com.bocaditos.service.production.ProductionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
class ProductionServiceTest {

    @Mock
    private ProductionSessionRepository productionSessionRepository;

    @Mock
    private ProductionWorkerRepository productionWorkerRepository;

    @InjectMocks
    private ProductionService productionService;

    @Test
    void createSessionRejectsInvalidTimeWindow() {
        ProductionSessionForm form = new ProductionSessionForm();
        form.setProductionDate(LocalDate.now());
        form.setStartTime(LocalTime.of(12, 0));
        form.setEndTime(LocalTime.of(11, 0));
        form.setUnitsProduced(100);
        form.setHourlyLaborRate(new BigDecimal("18.00"));
        form.setOtherCost(BigDecimal.ZERO);

        ProductionWorkerEntryForm worker = new ProductionWorkerEntryForm();
        worker.setWorkerId(1L);
        worker.setHoursWorked(new BigDecimal("2.00"));
        worker.setHourlyRate(new BigDecimal("18.00"));
        form.setWorkers(List.of(worker));

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "sessionForm");
        var session = productionService.createSession(form, bindingResult);

        assertThat(session).isNull();
        assertThat(bindingResult.hasFieldErrors("endTime")).isTrue();
    }

    @Test
    void createSessionCreatesWorkerCosts() {
        ProductionWorker worker = new ProductionWorker();
        worker.setFullName("Jose");
        worker.setDefaultHourlyRate(new BigDecimal("16.50"));
        when(productionWorkerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(productionSessionRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductionSessionForm form = new ProductionSessionForm();
        form.setProductionDate(LocalDate.now());
        form.setStartTime(LocalTime.of(8, 0));
        form.setEndTime(LocalTime.of(10, 0));
        form.setUnitsProduced(80);
        form.setHourlyLaborRate(new BigDecimal("17.00"));
        form.setOtherCost(new BigDecimal("10.00"));

        ProductionWorkerEntryForm entry = new ProductionWorkerEntryForm();
        entry.setWorkerId(1L);
        entry.setHoursWorked(new BigDecimal("2.00"));
        entry.setHourlyRate(new BigDecimal("16.50"));
        form.setWorkers(List.of(entry));

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "sessionForm");
        var session = productionService.createSession(form, bindingResult);

        assertThat(bindingResult.hasErrors()).isFalse();
        assertThat(session).isNotNull();
        assertThat(session.getSessionWorkers()).hasSize(1);
        assertThat(session.getSessionWorkers().get(0).getLaborCost()).isEqualByComparingTo("33.00");
    }
}
