package com.bocaditos.repository;

import com.bocaditos.domain.production.ProductionSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionSessionRepository extends JpaRepository<ProductionSession, Long> {
}
