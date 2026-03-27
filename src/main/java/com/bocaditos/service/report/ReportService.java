package com.bocaditos.service.report;

import com.bocaditos.domain.expense.Expense;
import com.bocaditos.domain.production.ProductionSession;
import com.bocaditos.domain.sales.SaleOrder;
import com.bocaditos.dto.report.ReportFilter;
import com.bocaditos.dto.report.ReportLineItem;
import com.bocaditos.dto.report.ReportsViewModel;
import com.bocaditos.repository.ExpenseRepository;
import com.bocaditos.repository.ProductionSessionRepository;
import com.bocaditos.repository.SaleOrderRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final SaleOrderRepository saleOrderRepository;
    private final ExpenseRepository expenseRepository;
    private final ProductionSessionRepository productionSessionRepository;

    public ReportService(
            SaleOrderRepository saleOrderRepository,
            ExpenseRepository expenseRepository,
            ProductionSessionRepository productionSessionRepository
    ) {
        this.saleOrderRepository = saleOrderRepository;
        this.expenseRepository = expenseRepository;
        this.productionSessionRepository = productionSessionRepository;
    }

    public ReportFilter defaultFilter() {
        ReportFilter filter = new ReportFilter();
        filter.setStartDate(LocalDate.now().withDayOfMonth(1));
        filter.setEndDate(LocalDate.now());
        return filter;
    }

    public ReportsViewModel buildReports(ReportFilter filter) {
        List<SaleOrder> sales = saleOrderRepository.findForDateRange(filter.getStartDate(), filter.getEndDate());
        List<Expense> expenses = expenseRepository.findForList(null, null, filter.getStartDate(), filter.getEndDate());
        List<ProductionSession> sessions = productionSessionRepository.findForList(filter.getStartDate(), filter.getEndDate());

        BigDecimal salesTotal = sales.stream().map(SaleOrder::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expenseTotal = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ReportLineItem> partnerSummary = sales.stream()
                .collect(Collectors.groupingBy(order -> order.getPartner().getFirstName() + " " + order.getPartner().getLastName(),
                        Collectors.mapping(SaleOrder::getTotalAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(entry -> new ReportLineItem(entry.getKey(), money(entry.getValue()), "Sales owner"))
                .toList();

        List<ReportLineItem> customerSummary = sales.stream()
                .filter(order -> order.getCustomer() != null)
                .collect(Collectors.groupingBy(order -> order.getCustomer().getFullName(),
                        Collectors.mapping(SaleOrder::getTotalAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(entry -> new ReportLineItem(entry.getKey(), money(entry.getValue()), "Customer sales"))
                .toList();

        List<ReportLineItem> expenseByCategory = expenses.stream()
                .collect(Collectors.groupingBy(expense -> expense.getCategory().getName(),
                        Collectors.mapping(Expense::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(entry -> new ReportLineItem(entry.getKey(), money(entry.getValue()), "Category spend"))
                .toList();

        List<ReportLineItem> productionEfficiency = sessions.stream()
                .sorted(Comparator.comparing(ProductionSession::getProductionDate).reversed())
                .map(session -> {
                    BigDecimal laborHours = session.getSessionWorkers().stream()
                            .map(worker -> worker.getHoursWorked())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal unitsPerHour = laborHours.compareTo(BigDecimal.ZERO) > 0
                            ? BigDecimal.valueOf(session.getUnitsProduced()).divide(laborHours, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    return new ReportLineItem(session.getProductionDate().toString(), session.getUnitsProduced() + " units", unitsPerHour + " units/hr");
                })
                .toList();

        return new ReportsViewModel(
                money(salesTotal),
                money(expenseTotal),
                money(salesTotal.subtract(expenseTotal)),
                partnerSummary,
                customerSummary,
                expenseByCategory,
                productionEfficiency
        );
    }

    private String money(BigDecimal value) {
        return "$" + value.setScale(2, RoundingMode.HALF_UP);
    }
}
