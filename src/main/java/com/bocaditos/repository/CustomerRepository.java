package com.bocaditos.repository;

import com.bocaditos.domain.customer.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByFullNameContainingIgnoreCaseOrderByFullNameAsc(String query);

    List<Customer> findAllByOrderByFullNameAsc();
}
