package com.bocaditos.repository;

import com.bocaditos.domain.production.ProductionWorker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionWorkerRepository extends JpaRepository<ProductionWorker, Long> {
}
