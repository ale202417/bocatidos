package com.bocaditos.repository;

import com.bocaditos.domain.expense.Expense;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("select coalesce(sum(e.amount), 0) from Expense e where e.partner.id = :partnerId")
    BigDecimal sumAmountByPartnerId(@Param("partnerId") Long partnerId);
}
