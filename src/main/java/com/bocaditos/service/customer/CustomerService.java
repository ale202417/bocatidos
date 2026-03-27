package com.bocaditos.service.customer;

import com.bocaditos.domain.customer.Customer;
import com.bocaditos.dto.customer.CustomerDetailView;
import com.bocaditos.dto.customer.CustomerForm;
import com.bocaditos.dto.customer.CustomerListItem;
import com.bocaditos.repository.CustomerRepository;
import com.bocaditos.repository.SaleOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final SaleOrderRepository saleOrderRepository;

    public CustomerService(CustomerRepository customerRepository, SaleOrderRepository saleOrderRepository) {
        this.customerRepository = customerRepository;
        this.saleOrderRepository = saleOrderRepository;
    }

    public List<CustomerListItem> getCustomers(String query) {
        List<Customer> customers = StringUtils.hasText(query)
                ? customerRepository.findByFullNameContainingIgnoreCaseOrderByFullNameAsc(query)
                : customerRepository.findAllByOrderByFullNameAsc();

        return customers.stream()
                .map(this::toListItem)
                .toList();
    }

    public CustomerDetailView getCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        return new CustomerDetailView(
                customer.getId(),
                customer.getFullName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getNotes(),
                saleOrderRepository.countByCustomerId(id),
                saleOrderRepository.sumTotalAmountByCustomerId(id)
        );
    }

    @Transactional
    public Customer createCustomer(CustomerForm form) {
        Customer customer = new Customer();
        customer.setFullName(form.getFullName().trim());
        customer.setPhone(form.getPhone());
        customer.setEmail(form.getEmail());
        customer.setAddress(form.getAddress());
        customer.setNotes(form.getNotes());
        return customerRepository.save(customer);
    }

    public CustomerForm emptyForm() {
        return new CustomerForm();
    }

    private CustomerListItem toListItem(Customer customer) {
        Long customerId = customer.getId();
        return new CustomerListItem(
                customerId,
                customer.getFullName(),
                customer.getPhone(),
                customer.getEmail(),
                saleOrderRepository.countByCustomerId(customerId),
                saleOrderRepository.sumTotalAmountByCustomerId(customerId)
        );
    }
}
