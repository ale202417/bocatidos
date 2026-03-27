package com.bocaditos.controller.customer;

import com.bocaditos.dto.customer.CustomerForm;
import com.bocaditos.service.customer.CustomerService;
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
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String listCustomers(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("pageTitle", "Customers");
        model.addAttribute("pageSubtitle", "Keep customer contacts, order history, and repeat business visible.");
        model.addAttribute("activeNav", "customers");
        model.addAttribute("searchQuery", q == null ? "" : q);
        model.addAttribute("customers", customerService.getCustomers(q));
        return "customers/list";
    }

    @GetMapping("/new")
    public String newCustomer(Model model) {
        model.addAttribute("pageTitle", "New Customer");
        model.addAttribute("pageSubtitle", "Capture customer details for direct orders and catering relationships.");
        model.addAttribute("activeNav", "customers");
        model.addAttribute("customerForm", customerService.emptyForm());
        return "customers/form";
    }

    @PostMapping
    public String createCustomer(
            @Valid @ModelAttribute("customerForm") CustomerForm customerForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "New Customer");
            model.addAttribute("pageSubtitle", "Capture customer details for direct orders and catering relationships.");
            model.addAttribute("activeNav", "customers");
            return "customers/form";
        }

        var savedCustomer = customerService.createCustomer(customerForm);
        redirectAttributes.addFlashAttribute("flashMessage", "Customer created successfully.");
        return "redirect:/customers/" + savedCustomer.getId();
    }

    @GetMapping("/{id}")
    public String viewCustomer(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Customer Profile");
        model.addAttribute("pageSubtitle", "Contact details, purchasing history, and account context for repeat orders.");
        model.addAttribute("activeNav", "customers");
        model.addAttribute("customer", customerService.getCustomer(id));
        return "customers/detail";
    }
}
