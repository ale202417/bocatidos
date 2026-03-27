package com.bocaditos.controller.report;

import com.bocaditos.dto.report.ReportFilter;
import com.bocaditos.service.report.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String reports(@ModelAttribute("filter") ReportFilter filter, Model model) {
        if (filter.getStartDate() == null && filter.getEndDate() == null) {
            ReportFilter defaultFilter = reportService.defaultFilter();
            filter.setStartDate(defaultFilter.getStartDate());
            filter.setEndDate(defaultFilter.getEndDate());
        }
        model.addAttribute("pageTitle", "Reports");
        model.addAttribute("pageSubtitle", "Business summaries across sales, expenses, partner performance, and production efficiency.");
        model.addAttribute("activeNav", "reports");
        model.addAttribute("reports", reportService.buildReports(filter));
        return "reports/index";
    }
}
