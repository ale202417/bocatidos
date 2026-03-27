package com.bocaditos.controller.production;

import com.bocaditos.dto.production.ProductionFilter;
import com.bocaditos.dto.production.ProductionSessionForm;
import com.bocaditos.service.production.ProductionService;
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
@RequestMapping("/production")
public class ProductionController {

    private final ProductionService productionService;

    public ProductionController(ProductionService productionService) {
        this.productionService = productionService;
    }

    @GetMapping
    public String listSessions(@ModelAttribute("filter") ProductionFilter filter, Model model) {
        model.addAttribute("pageTitle", "Production");
        model.addAttribute("pageSubtitle", "Track batch sessions, labor effort, and output efficiency.");
        model.addAttribute("activeNav", "production");
        model.addAttribute("sessions", productionService.getSessions(filter));
        model.addAttribute("summary", productionService.getSummary(filter));
        return "production/list";
    }

    @GetMapping("/new")
    public String newSession(Model model) {
        configureForm(model, productionService.emptyForm());
        return "production/form";
    }

    @PostMapping
    public String createSession(
            @Valid @ModelAttribute("sessionForm") ProductionSessionForm sessionForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            configureForm(model, sessionForm);
            return "production/form";
        }
        var session = productionService.createSession(sessionForm, bindingResult);
        if (bindingResult.hasErrors()) {
            configureForm(model, sessionForm);
            return "production/form";
        }
        redirectAttributes.addFlashAttribute("flashMessage", "Production session saved successfully.");
        return "redirect:/production/" + session.getId();
    }

    @GetMapping("/{id}")
    public String viewSession(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Production Session");
        model.addAttribute("pageSubtitle", "Review workers, labor cost, and throughput for this batch.");
        model.addAttribute("activeNav", "production");
        model.addAttribute("session", productionService.getSession(id));
        return "production/detail";
    }

    @PostMapping("/{id}/delete")
    public String deleteSession(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productionService.deleteSession(id);
        redirectAttributes.addFlashAttribute("flashMessage", "Production session deleted successfully.");
        return "redirect:/production";
    }

    private void configureForm(Model model, ProductionSessionForm form) {
        model.addAttribute("pageTitle", "New Production Session");
        model.addAttribute("pageSubtitle", "Record staffing, timing, cost, and output for a production run.");
        model.addAttribute("activeNav", "production");
        model.addAttribute("sessionForm", form);
        model.addAttribute("formOptions", productionService.getFormOptions());
    }
}
