package com.bocaditos.repository;

import com.bocaditos.domain.sales.SaleOrder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleOrderRepository extends JpaRepository<SaleOrder, Long> {

    @Query("""
            select distinct s from SaleOrder s
            left join fetch s.partner p
            left join fetch s.customer c
            left join fetch s.items i
            where lower(s.orderNumber) like lower(concat('%', :query, '%'))
               or lower(p.firstName) like lower(concat('%', :query, '%'))
               or lower(p.lastName) like lower(concat('%', :query, '%'))
               or lower(coalesce(c.fullName, '')) like lower(concat('%', :query, '%'))
            order by s.orderDate desc, s.createdAt desc
            """)
    List<SaleOrder> searchForList(@Param("query") String query);

    @Query("""
            select distinct s from SaleOrder s
            left join fetch s.partner
            left join fetch s.customer
            left join fetch s.items
            order by s.orderDate desc, s.createdAt desc
            """)
    List<SaleOrder> findAllForList();

    @Query("""
            select distinct s from SaleOrder s
            left join fetch s.partner
            left join fetch s.customer
            left join fetch s.items
            where (:startDate is null or s.orderDate >= :startDate)
              and (:endDate is null or s.orderDate <= :endDate)
            order by s.orderDate desc, s.createdAt desc
            """)
    List<SaleOrder> findForDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
            select distinct s from SaleOrder s
            left join fetch s.partner
            left join fetch s.customer
            left join fetch s.items
            where s.id = :id
            """)
    Optional<SaleOrder> findDetailedById(@Param("id") Long id);

    long countByPartnerId(Long partnerId);

    long countByCustomerId(Long customerId);

    @Query("select coalesce(sum(s.totalAmount), 0) from SaleOrder s where s.partner.id = :partnerId")
    BigDecimal sumTotalAmountByPartnerId(@Param("partnerId") Long partnerId);

    @Query("select coalesce(sum(s.totalAmount), 0) from SaleOrder s where s.customer.id = :customerId")
    BigDecimal sumTotalAmountByCustomerId(@Param("customerId") Long customerId);

    @Query("""
            select coalesce(sum(s.totalAmount), 0) from SaleOrder s
            where (:startDate is null or s.orderDate >= :startDate)
              and (:endDate is null or s.orderDate <= :endDate)
            """)
    BigDecimal sumTotalAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
