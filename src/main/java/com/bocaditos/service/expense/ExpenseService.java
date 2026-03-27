package com.bocaditos.service.expense;

import com.bocaditos.domain.expense.Expense;
import com.bocaditos.dto.expense.ExpenseDetailView;
import com.bocaditos.dto.expense.ExpenseFilter;
import com.bocaditos.dto.expense.ExpenseForm;
import com.bocaditos.dto.expense.ExpenseFormOptions;
import com.bocaditos.dto.expense.ExpenseListItem;
import com.bocaditos.dto.expense.ExpenseSummaryView;
import com.bocaditos.repository.ExpenseCategoryRepository;
import com.bocaditos.repository.ExpenseRepository;
import com.bocaditos.repository.PartnerRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final PartnerRepository partnerRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseCategoryRepository expenseCategoryRepository,
            PartnerRepository partnerRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.partnerRepository = partnerRepository;
    }

    public List<ExpenseListItem> getExpenses(ExpenseFilter filter) {
        return expenseRepository.findForList(filter.getType(), filter.getCategoryId(), filter.getStartDate(), filter.getEndDate()).stream()
                .map(expense -> new ExpenseListItem(
                        expense.getId(),
                        expense.getExpenseDate(),
                        expense.getCategory().getName(),
                        expense.getPartner() != null ? expense.getPartner().getFirstName() + " " + expense.getPartner().getLastName() : "Shared / business-wide",
                        expense.getType(),
                        expense.getAmount(),
                        expense.getDescription()
                ))
                .toList();
    }

    public ExpenseDetailView getExpense(Long id) {
        Expense expense = expenseRepository.findDetailedById(id)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));

        return new ExpenseDetailView(
                expense.getId(),
                expense.getExpenseDate(),
                expense.getCategory().getName(),
                expense.getPartner() != null ? expense.getPartner().getFirstName() + " " + expense.getPartner().getLastName() : "Shared / business-wide",
                expense.getType(),
                expense.getAmount(),
                expense.getDescription(),
                expense.getNotes()
        );
    }

    public ExpenseFormOptions getFormOptions() {
        return new ExpenseFormOptions(
                expenseCategoryRepository.findAllByOrderByNameAsc().stream()
                        .map(category -> new ExpenseFormOptions.ReferenceOption(category.getId(), category.getName()))
                        .toList(),
                partnerRepository.findAllByOrderByLastNameAscFirstNameAsc().stream()
                        .map(partner -> new ExpenseFormOptions.ReferenceOption(partner.getId(), partner.getFirstName() + " " + partner.getLastName()))
                        .toList()
        );
    }

    public ExpenseSummaryView getSummary(ExpenseFilter filter) {
        return new ExpenseSummaryView(
                expenseRepository.sumAmountByFilters(filter.getType(), filter.getStartDate(), filter.getEndDate()),
                expenseRepository.countByType(com.bocaditos.domain.expense.ExpenseType.REIMBURSEMENT),
                expenseRepository.countByType(com.bocaditos.domain.expense.ExpenseType.BUSINESS_EXPENSE)
        );
    }

    public ExpenseForm emptyForm() {
        return new ExpenseForm();
    }

    @Transactional
    public Expense createExpense(ExpenseForm form) {
        Expense expense = new Expense();
        expense.setExpenseDate(form.getExpenseDate());
        expense.setCategory(expenseCategoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Expense category not found")));
        if (form.getPartnerId() != null) {
            expense.setPartner(partnerRepository.findById(form.getPartnerId())
                    .orElseThrow(() -> new EntityNotFoundException("Partner not found")));
        }
        expense.setType(form.getType());
        expense.setAmount(form.getAmount());
        expense.setDescription(form.getDescription().trim());
        expense.setNotes(form.getNotes());
        return expenseRepository.save(expense);
    }

    @Transactional
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new EntityNotFoundException("Expense not found");
        }
        expenseRepository.deleteById(id);
    }
}
