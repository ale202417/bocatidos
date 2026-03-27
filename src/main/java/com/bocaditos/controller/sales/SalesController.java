package com.bocaditos.controller.sales;

import com.bocaditos.domain.sales.PaymentMethod;
import com.bocaditos.domain.sales.PaymentStatus;
import com.bocaditos.dto.sales.SaleOrderForm;
import com.bocaditos.service.sales.SalesService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sales")
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping
    public String listOrders(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("pageTitle", "Orders & Sales");
        model.addAttribute("pageSubtitle", "Capture orders, monitor payment status, and tie revenue back to partners and customers.");
        model.addAttribute("activeNav", "sales");
        model.addAttribute("searchQuery", q == null ? "" : q);
        model.addAttribute("orders", salesService.getOrders(q));
        return "sales/list";
    }

    @GetMapping("/new")
    public String newOrder(Model model) {
        configureFormPage(model, salesService.emptyForm());
        return "sales/form";
    }

    @PostMapping
    public String createOrder(
            @Valid @ModelAttribute("saleOrderForm") SaleOrderForm saleOrderForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (saleOrderForm.getItems() == null || saleOrderForm.getItems().isEmpty()) {
            saleOrderForm.setItems(salesService.emptyForm().getItems());
        }

        if (bindingResult.hasErrors()) {
            configureFormPage(model, saleOrderForm);
            return "sales/form";
        }

        var savedOrder = salesService.createOrder(saleOrderForm, bindingResult);
        if (bindingResult.hasErrors()) {
            configureFormPage(model, saleOrderForm);
            return "sales/form";
        }

        redirectAttributes.addFlashAttribute("flashMessage", "Order saved successfully.");
        return "redirect:/sales/" + savedOrder.getId();
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Order Details");
        model.addAttribute("pageSubtitle", "Review line items, payment position, and linked partner/customer context.");
        model.addAttribute("activeNav", "sales");
        model.addAttribute("order", salesService.getOrder(id));
        return "sales/detail";
    }

    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        salesService.deleteOrder(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Order deleted successfully.");
        return "redirect:/sales";
    }

    private void configureFormPage(Model model, SaleOrderForm form) {
        model.addAttribute("pageTitle", "New Order");
        model.addAttribute("pageSubtitle", "Record a sale with line items, payment progress, and customer context.");
        model.addAttribute("activeNav", "sales");
        model.addAttribute("saleOrderForm", form);
        model.addAttribute("paymentStatuses", PaymentStatus.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("formOptions", salesService.getFormOptions());
    }
}
