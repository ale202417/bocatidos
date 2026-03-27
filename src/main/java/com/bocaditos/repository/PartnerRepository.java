package com.bocaditos.repository;

import com.bocaditos.domain.partner.Partner;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

    List<Partner> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrderByLastNameAscFirstNameAsc(
            String firstNameQuery,
            String lastNameQuery
    );

    List<Partner> findAllByOrderByLastNameAscFirstNameAsc();

    long countByStatus(com.bocaditos.domain.partner.PartnerStatus status);
}
