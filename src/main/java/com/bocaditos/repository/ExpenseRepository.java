package com.bocaditos.repository;

import com.bocaditos.domain.expense.Expense;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("""
            select e from Expense e
            left join fetch e.category
            left join fetch e.partner
            where (:type is null or e.type = :type)
              and (:categoryId is null or e.category.id = :categoryId)
              and (:startDate is null or e.expenseDate >= :startDate)
              and (:endDate is null or e.expenseDate <= :endDate)
            order by e.expenseDate desc, e.createdAt desc
            """)
    List<Expense> findForList(
            @Param("type") com.bocaditos.domain.expense.ExpenseType type,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            select e from Expense e
            left join fetch e.category
            left join fetch e.partner
            where e.id = :id
            """)
    Optional<Expense> findDetailedById(@Param("id") Long id);

    @Query("select coalesce(sum(e.amount), 0) from Expense e where e.partner.id = :partnerId")
    BigDecimal sumAmountByPartnerId(@Param("partnerId") Long partnerId);

    @Query("""
            select coalesce(sum(e.amount), 0) from Expense e
            where (:type is null or e.type = :type)
              and (:startDate is null or e.expenseDate >= :startDate)
              and (:endDate is null or e.expenseDate <= :endDate)
            """)
    BigDecimal sumAmountByFilters(
            @Param("type") com.bocaditos.domain.expense.ExpenseType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    long countByType(com.bocaditos.domain.expense.ExpenseType type);
}
