package com.bocaditos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.bocaditos.domain.customer.Customer;
import com.bocaditos.domain.expense.Expense;
import com.bocaditos.domain.expense.ExpenseCategory;
import com.bocaditos.domain.partner.Partner;
import com.bocaditos.domain.production.ProductionSession;
import com.bocaditos.domain.production.ProductionSessionWorker;
import com.bocaditos.domain.sales.SaleOrder;
import com.bocaditos.dto.report.ReportFilter;
import com.bocaditos.dto.report.ReportsViewModel;
import com.bocaditos.repository.ExpenseRepository;
import com.bocaditos.repository.ProductionSessionRepository;
import com.bocaditos.repository.SaleOrderRepository;
import com.bocaditos.service.report.ReportService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private SaleOrderRepository saleOrderRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ProductionSessionRepository productionSessionRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void buildReportsAggregatesCoreTotals() {
        Partner partner = new Partner();
        partner.setFirstName("Ana");
        partner.setLastName("Lopez");
        Customer customer = new Customer();
        customer.setFullName("Maria");

        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setPartner(partner);
        saleOrder.setCustomer(customer);
        saleOrder.setOrderDate(LocalDate.now());
        saleOrder.setTotalAmount(new BigDecimal("120.00"));
        saleOrder.setAmountPaid(new BigDecimal("80.00"));

        ExpenseCategory category = new ExpenseCategory();
        category.setName("Ingredients");
        Expense expense = new Expense();
        expense.setCategory(category);
        expense.setAmount(new BigDecimal("45.00"));

        ProductionSession session = new ProductionSession();
        session.setProductionDate(LocalDate.now());
        session.setUnitsProduced(100);
        session.setStartTime(LocalTime.of(8, 0));
        session.setEndTime(LocalTime.of(10, 0));
        ProductionSessionWorker worker = new ProductionSessionWorker();
        worker.setHoursWorked(new BigDecimal("2.00"));
        worker.setLaborCost(new BigDecimal("36.00"));
        session.getSessionWorkers().add(worker);

        when(saleOrderRepository.findForDateRange(null, null)).thenReturn(List.of(saleOrder));
        when(expenseRepository.findForList(null, null, null, null)).thenReturn(List.of(expense));
        when(productionSessionRepository.findForList(null, null)).thenReturn(List.of(session));

        ReportsViewModel reports = reportService.buildReports(new ReportFilter());

        assertThat(reports.salesTotal()).isEqualTo("$120.00");
        assertThat(reports.expenseTotal()).isEqualTo("$45.00");
        assertThat(reports.netOperatingPosition()).isEqualTo("$75.00");
        assertThat(reports.partnerSummary()).hasSize(1);
        assertThat(reports.expenseByCategory()).hasSize(1);
        assertThat(reports.productionEfficiency()).hasSize(1);
    }
}
