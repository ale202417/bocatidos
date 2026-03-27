package com.bocaditos.controller.dashboard;

import com.bocaditos.service.dashboard.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Operations Dashboard");
        model.addAttribute("pageSubtitle", "A live snapshot of sales, production, and operating costs.");
        model.addAttribute("activeNav", "dashboard");
        model.addAttribute("dashboard", dashboardService.getDashboardView());
        return "dashboard/index";
    }
}
