package com.bocaditos.controller.expense;

import com.bocaditos.domain.expense.ExpenseType;
import com.bocaditos.dto.expense.ExpenseFilter;
import com.bocaditos.dto.expense.ExpenseForm;
import com.bocaditos.service.expense.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public String listExpenses(@ModelAttribute("filter") ExpenseFilter filter, Model model) {
        model.addAttribute("pageTitle", "Expenses & Reimbursements");
        model.addAttribute("pageSubtitle", "Track spending, partner reimbursements, and where operating costs are going.");
        model.addAttribute("activeNav", "expenses");
        model.addAttribute("expenses", expenseService.getExpenses(filter));
        model.addAttribute("summary", expenseService.getSummary(filter));
        model.addAttribute("categories", expenseService.getFormOptions().categories());
        model.addAttribute("types", ExpenseType.values());
        return "expenses/list";
    }

    @GetMapping("/new")
    public String newExpense(Model model) {
        configureForm(model, expenseService.emptyForm());
        return "expenses/form";
    }

    @PostMapping
    public String createExpense(
            @Valid @ModelAttribute("expenseForm") ExpenseForm expenseForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            configureForm(model, expenseForm);
            return "expenses/form";
        }

        var expense = expenseService.createExpense(expenseForm);
        redirectAttributes.addFlashAttribute("flashMessage", "Expense saved successfully.");
        return "redirect:/expenses/" + expense.getId();
    }

    @GetMapping("/{id}")
    public String viewExpense(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Expense Detail");
        model.addAttribute("pageSubtitle", "Review category, amount, and accountability context for this entry.");
        model.addAttribute("activeNav", "expenses");
        model.addAttribute("expense", expenseService.getExpense(id));
        return "expenses/detail";
    }

    @PostMapping("/{id}/delete")
    public String deleteExpense(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        expenseService.deleteExpense(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Expense deleted successfully.");
        return "redirect:/expenses";
    }

    private void configureForm(Model model, ExpenseForm form) {
        model.addAttribute("pageTitle", "New Expense");
        model.addAttribute("pageSubtitle", "Capture a business cost or partner reimbursement with the right category and owner.");
        model.addAttribute("activeNav", "expenses");
        model.addAttribute("expenseForm", form);
        model.addAttribute("formOptions", expenseService.getFormOptions());
        model.addAttribute("types", ExpenseType.values());
    }
}
