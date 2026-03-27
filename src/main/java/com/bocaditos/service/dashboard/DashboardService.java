package com.bocaditos.service.dashboard;

import com.bocaditos.dto.dashboard.DashboardMetricCard;
import com.bocaditos.dto.dashboard.DashboardViewModel;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    public DashboardViewModel getDashboardView() {
        return new DashboardViewModel(
                List.of(
                        new DashboardMetricCard("Monthly sales", "$8,420", "+12.4%", "up", "Compared with last month"),
                        new DashboardMetricCard("Open balances", "$1,160", "-8.0%", "down", "Unpaid and partially paid orders"),
                        new DashboardMetricCard("Production output", "612 units", "+6.1%", "up", "This week's empanadas produced"),
                        new DashboardMetricCard("Expense run rate", "$2,140", "+3.2%", "neutral", "Ingredient, labor, and delivery costs")
                ),
                List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                List.of(920, 1140, 860, 1260, 1580, 1420, 1240),
                List.of("Ingredients", "Labor", "Packaging", "Transport"),
                List.of(940, 620, 310, 270)
        );
    }
}
