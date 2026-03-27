package com.bocaditos.repository;

import com.bocaditos.domain.expense.ExpenseCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

    Optional<ExpenseCategory> findByNameIgnoreCase(String name);

    List<ExpenseCategory> findAllByOrderByNameAsc();
}
