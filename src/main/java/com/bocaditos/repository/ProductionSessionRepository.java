package com.bocaditos.repository;

import com.bocaditos.domain.production.ProductionSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductionSessionRepository extends JpaRepository<ProductionSession, Long> {

    @Query("""
            select distinct ps from ProductionSession ps
            left join fetch ps.sessionWorkers sw
            left join fetch sw.worker
            where (:startDate is null or ps.productionDate >= :startDate)
              and (:endDate is null or ps.productionDate <= :endDate)
            order by ps.productionDate desc, ps.createdAt desc
            """)
    List<ProductionSession> findForList(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
            select distinct ps from ProductionSession ps
            left join fetch ps.sessionWorkers sw
            left join fetch sw.worker
            where ps.id = :id
            """)
    Optional<ProductionSession> findDetailedById(@Param("id") Long id);
}
