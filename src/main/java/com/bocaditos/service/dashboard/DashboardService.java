package com.bocaditos.service.dashboard;

import com.bocaditos.domain.expense.Expense;
import com.bocaditos.domain.production.ProductionSession;
import com.bocaditos.domain.sales.SaleOrder;
import com.bocaditos.dto.dashboard.DashboardActivityItem;
import com.bocaditos.dto.dashboard.DashboardMetricCard;
import com.bocaditos.dto.dashboard.DashboardRankedItem;
import com.bocaditos.dto.dashboard.DashboardViewModel;
import com.bocaditos.repository.ExpenseRepository;
import com.bocaditos.repository.ProductionSessionRepository;
import com.bocaditos.repository.SaleOrderRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final SaleOrderRepository saleOrderRepository;
    private final ExpenseRepository expenseRepository;
    private final ProductionSessionRepository productionSessionRepository;

    public DashboardService(
            SaleOrderRepository saleOrderRepository,
            ExpenseRepository expenseRepository,
            ProductionSessionRepository productionSessionRepository
    ) {
        this.saleOrderRepository = saleOrderRepository;
        this.expenseRepository = expenseRepository;
        this.productionSessionRepository = productionSessionRepository;
    }

    public DashboardViewModel getDashboardView() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        LocalDate monthStart = today.withDayOfMonth(1);

        List<SaleOrder> weeklySales = saleOrderRepository.findForDateRange(weekStart, today);
        List<Expense> monthlyExpenses = expenseRepository.findForList(null, null, monthStart, today);
        List<ProductionSession> weeklyProduction = productionSessionRepository.findForList(weekStart, today);

        BigDecimal monthlySalesTotal = saleOrderRepository.sumTotalAmountByDateRange(monthStart, today);
        BigDecimal openBalances = weeklySales.stream()
                .map(order -> order.getTotalAmount().subtract(order.getAmountPaid()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int producedUnits = weeklyProduction.stream().mapToInt(ProductionSession::getUnitsProduced).sum();
        BigDecimal expenseRunRate = monthlyExpenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<String> salesLabels = new ArrayList<>();
        List<Integer> salesValues = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            salesLabels.add(day.getDayOfWeek().name().substring(0, 1) + day.getDayOfWeek().name().substring(1, 3).toLowerCase());
            BigDecimal total = weeklySales.stream()
                    .filter(order -> order.getOrderDate().equals(day))
                    .map(SaleOrder::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            salesValues.add(total.setScale(0, RoundingMode.HALF_UP).intValue());
        }

        Map<String, BigDecimal> expensesByCategory = new LinkedHashMap<>();
        for (Expense expense : monthlyExpenses) {
            expensesByCategory.merge(expense.getCategory().getName(), expense.getAmount(), BigDecimal::add);
        }

        List<DashboardRankedItem> topCustomers = weeklySales.stream()
                .filter(order -> order.getCustomer() != null)
                .collect(java.util.stream.Collectors.groupingBy(order -> order.getCustomer().getFullName(),
                        java.util.stream.Collectors.mapping(SaleOrder::getTotalAmount,
                                java.util.stream.Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(3)
                .map(entry -> new DashboardRankedItem(entry.getKey(), money(entry.getValue()), "Weekly customer sales"))
                .toList();

        List<DashboardActivityItem> recentActivity = new ArrayList<>();
        weeklySales.stream().limit(2).forEach(order -> recentActivity.add(
                new DashboardActivityItem(order.getOrderNumber(), money(order.getTotalAmount()) + " order linked to " + order.getPartner().getFirstName())
        ));
        monthlyExpenses.stream().limit(2).forEach(expense -> recentActivity.add(
                new DashboardActivityItem("Expense logged", expense.getDescription() + " for " + money(expense.getAmount()))
        ));
        weeklyProduction.stream().limit(2).forEach(session -> recentActivity.add(
                new DashboardActivityItem("Production session", session.getUnitsProduced() + " units on " + session.getProductionDate())
        ));

        return new DashboardViewModel(
                List.of(
                        new DashboardMetricCard("Monthly sales", money(monthlySalesTotal), "Live", "up", "Revenue booked this month"),
                        new DashboardMetricCard("Open balances", money(openBalances), "Follow-up", "down", "Unpaid and partially paid orders"),
                        new DashboardMetricCard("Production output", producedUnits + " units", "Last 7 days", "up", "Units completed this week"),
                        new DashboardMetricCard("Expense run rate", money(expenseRunRate), "Current month", "neutral", "Tracked operating spend")
                ),
                salesLabels,
                salesValues,
                new ArrayList<>(expensesByCategory.keySet()),
                expensesByCategory.values().stream().map(value -> value.setScale(0, RoundingMode.HALF_UP).intValue()).toList(),
                topCustomers,
                recentActivity.stream().limit(4).toList()
        );
    }

    private String money(BigDecimal value) {
        return "$" + value.setScale(2, RoundingMode.HALF_UP);
    }
}
