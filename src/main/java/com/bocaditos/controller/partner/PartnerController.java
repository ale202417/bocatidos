package com.bocaditos.controller.partner;

import com.bocaditos.domain.partner.PartnerStatus;
import com.bocaditos.dto.partner.PartnerForm;
import com.bocaditos.service.partner.PartnerService;
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
@RequestMapping("/partners")
public class PartnerController {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @GetMapping
    public String listPartners(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("pageTitle", "Partners");
        model.addAttribute("pageSubtitle", "Track socios, contribution visibility, and linked operating activity.");
        model.addAttribute("activeNav", "partners");
        model.addAttribute("searchQuery", q == null ? "" : q);
        model.addAttribute("partners", partnerService.getPartners(q));
        return "partners/list";
    }

    @GetMapping("/new")
    public String newPartner(Model model) {
        model.addAttribute("pageTitle", "New Partner");
        model.addAttribute("pageSubtitle", "Add a business partner with contact and operating details.");
        model.addAttribute("activeNav", "partners");
        model.addAttribute("partnerForm", partnerService.emptyForm());
        model.addAttribute("statuses", PartnerStatus.values());
        return "partners/form";
    }

    @PostMapping
    public String createPartner(
            @Valid @ModelAttribute("partnerForm") PartnerForm partnerForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "New Partner");
            model.addAttribute("pageSubtitle", "Add a business partner with contact and operating details.");
            model.addAttribute("activeNav", "partners");
            model.addAttribute("statuses", PartnerStatus.values());
            return "partners/form";
        }

        var savedPartner = partnerService.createPartner(partnerForm);
        redirectAttributes.addFlashAttribute("flashMessage", "Partner created successfully.");
        return "redirect:/partners/" + savedPartner.getId();
    }

    @GetMapping("/{id}")
    public String viewPartner(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Partner Profile");
        model.addAttribute("pageSubtitle", "Partner contribution, linked sales, and reimbursable activity at a glance.");
        model.addAttribute("activeNav", "partners");
        model.addAttribute("partner", partnerService.getPartner(id));
        return "partners/detail";
    }

    @PostMapping("/{id}/delete")
    public String deletePartner(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            partnerService.deletePartner(id);
            redirectAttributes.addFlashAttribute("flashMessage", "Partner deleted successfully.");
            return "redirect:/partners";
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("flashMessage", ex.getMessage());
            return "redirect:/partners/" + id;
        }
    }
}
