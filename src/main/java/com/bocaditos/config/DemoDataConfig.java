package com.bocaditos.config;

import com.bocaditos.domain.customer.Customer;
import com.bocaditos.domain.expense.Expense;
import com.bocaditos.domain.expense.ExpenseCategory;
import com.bocaditos.domain.expense.ExpenseType;
import com.bocaditos.domain.partner.Partner;
import com.bocaditos.domain.partner.PartnerStatus;
import com.bocaditos.domain.production.ProductionSession;
import com.bocaditos.domain.production.ProductionSessionWorker;
import com.bocaditos.domain.production.ProductionWorker;
import com.bocaditos.domain.sales.PaymentMethod;
import com.bocaditos.domain.sales.PaymentRecord;
import com.bocaditos.domain.sales.PaymentStatus;
import com.bocaditos.domain.sales.SaleOrder;
import com.bocaditos.domain.sales.SaleOrderItem;
import com.bocaditos.repository.CustomerRepository;
import com.bocaditos.repository.ExpenseCategoryRepository;
import com.bocaditos.repository.ExpenseRepository;
import com.bocaditos.repository.PartnerRepository;
import com.bocaditos.repository.ProductionSessionRepository;
import com.bocaditos.repository.ProductionWorkerRepository;
import com.bocaditos.repository.SaleOrderRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("demo")
public class DemoDataConfig {

    @Bean
    CommandLineRunner seedDemoData(
            PartnerRepository partnerRepository,
            CustomerRepository customerRepository,
            ExpenseCategoryRepository expenseCategoryRepository,
            ExpenseRepository expenseRepository,
            SaleOrderRepository saleOrderRepository,
            ProductionWorkerRepository productionWorkerRepository,
            ProductionSessionRepository productionSessionRepository
    ) {
        return args -> {
            if (partnerRepository.count() > 0) {
                return;
            }

            Partner rosa = new Partner();
            rosa.setFirstName("Rosa");
            rosa.setLastName("Martinez");
            rosa.setPhone("(813) 555-0132");
            rosa.setEmail("rosa@bocaditos.local");
            rosa.setStatus(PartnerStatus.ACTIVE);
            rosa.setNotes("Focuses on production planning and ingredient purchasing.");
            partnerRepository.save(rosa);

            Partner ana = new Partner();
            ana.setFirstName("Ana");
            ana.setLastName("Lopez");
            ana.setPhone("(813) 555-0184");
            ana.setEmail("ana@bocaditos.local");
            ana.setStatus(PartnerStatus.ACTIVE);
            ana.setNotes("Handles sales coordination, catering clients, and weekend pickups.");
            partnerRepository.save(ana);

            Customer customer1 = new Customer();
            customer1.setFullName("Maria Gonzalez");
            customer1.setPhone("(813) 555-0147");
            customer1.setEmail("maria.g@example.com");
            customer1.setAddress("412 Citrus Ave, Tampa, FL");
            customer1.setNotes("Frequent family orders every other Friday.");
            customerRepository.save(customer1);

            Customer customer2 = new Customer();
            customer2.setFullName("South Campus Student Org");
            customer2.setPhone("(813) 555-0192");
            customer2.setEmail("events@southcampus.org");
            customer2.setAddress("100 College Way, Tampa, FL");
            customer2.setNotes("Catering customer with larger batch orders.");
            customerRepository.save(customer2);

            ExpenseCategory ingredients = expenseCategoryRepository.findByNameIgnoreCase("Ingredients").orElseThrow();
            ExpenseCategory packaging = expenseCategoryRepository.findByNameIgnoreCase("Packaging").orElseThrow();

            SaleOrder order1 = new SaleOrder();
            order1.setOrderNumber("SO-1048");
            order1.setOrderDate(LocalDate.now().minusDays(2));
            order1.setPartner(ana);
            order1.setCustomer(customer1);
            order1.setPaymentStatus(PaymentStatus.PAID);
            order1.setSubtotal(new BigDecimal("86.00"));
            order1.setDiscountAmount(BigDecimal.ZERO);
            order1.setTotalAmount(new BigDecimal("86.00"));
            order1.setAmountPaid(new BigDecimal("86.00"));
            order1.setNotes("Mixed chicken and beef empanadas for family gathering.");

            SaleOrderItem order1Item1 = new SaleOrderItem();
            order1Item1.setSaleOrder(order1);
            order1Item1.setItemName("Chicken empanadas");
            order1Item1.setQuantity(20);
            order1Item1.setUnitPrice(new BigDecimal("2.50"));
            order1Item1.setLineTotal(new BigDecimal("50.00"));

            SaleOrderItem order1Item2 = new SaleOrderItem();
            order1Item2.setSaleOrder(order1);
            order1Item2.setItemName("Beef empanadas");
            order1Item2.setQuantity(12);
            order1Item2.setUnitPrice(new BigDecimal("3.00"));
            order1Item2.setLineTotal(new BigDecimal("36.00"));

            order1.getItems().add(order1Item1);
            order1.getItems().add(order1Item2);

            PaymentRecord payment1 = new PaymentRecord();
            payment1.setSaleOrder(order1);
            payment1.setPaymentDate(LocalDate.now().minusDays(2));
            payment1.setAmount(new BigDecimal("86.00"));
            payment1.setPaymentMethod(PaymentMethod.ZELLE);
            payment1.setNotes("Single payment received before pickup.");
            order1.getPayments().add(payment1);

            saleOrderRepository.save(order1);

            SaleOrder order2 = new SaleOrder();
            order2.setOrderNumber("SO-1049");
            order2.setOrderDate(LocalDate.now().minusDays(1));
            order2.setPartner(rosa);
            order2.setCustomer(customer2);
            order2.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
            order2.setSubtotal(new BigDecimal("240.00"));
            order2.setDiscountAmount(new BigDecimal("20.00"));
            order2.setTotalAmount(new BigDecimal("220.00"));
            order2.setAmountPaid(new BigDecimal("100.00"));
            order2.setNotes("Campus event order with discounted bulk pricing.");

            SaleOrderItem order2Item1 = new SaleOrderItem();
            order2Item1.setSaleOrder(order2);
            order2Item1.setItemName("Mini assorted empanadas");
            order2Item1.setQuantity(100);
            order2Item1.setUnitPrice(new BigDecimal("2.20"));
            order2Item1.setLineTotal(new BigDecimal("220.00"));
            order2.getItems().add(order2Item1);

            PaymentRecord payment2 = new PaymentRecord();
            payment2.setSaleOrder(order2);
            payment2.setPaymentDate(LocalDate.now().minusDays(1));
            payment2.setAmount(new BigDecimal("100.00"));
            payment2.setPaymentMethod(PaymentMethod.CASH);
            payment2.setNotes("Deposit collected at order confirmation.");
            order2.getPayments().add(payment2);

            saleOrderRepository.save(order2);

            Expense expense1 = new Expense();
            expense1.setExpenseDate(LocalDate.now().minusDays(3));
            expense1.setCategory(ingredients);
            expense1.setPartner(rosa);
            expense1.setType(ExpenseType.BUSINESS_EXPENSE);
            expense1.setAmount(new BigDecimal("145.60"));
            expense1.setDescription("Weekly meat, onions, and cheese purchase");
            expense1.setNotes("Purchased from local wholesale market.");
            expenseRepository.save(expense1);

            Expense expense2 = new Expense();
            expense2.setExpenseDate(LocalDate.now().minusDays(1));
            expense2.setCategory(packaging);
            expense2.setPartner(ana);
            expense2.setType(ExpenseType.REIMBURSEMENT);
            expense2.setAmount(new BigDecimal("48.25"));
            expense2.setDescription("Boxes, labels, and pickup bags");
            expense2.setNotes("Partner reimbursement submitted with receipt.");
            expenseRepository.save(expense2);

            ProductionWorker worker1 = new ProductionWorker();
            worker1.setFullName("Rosa Martinez");
            worker1.setDefaultHourlyRate(new BigDecimal("18.00"));
            productionWorkerRepository.save(worker1);

            ProductionWorker worker2 = new ProductionWorker();
            worker2.setFullName("Jose Perez");
            worker2.setDefaultHourlyRate(new BigDecimal("16.50"));
            productionWorkerRepository.save(worker2);

            ProductionSession session = new ProductionSession();
            session.setProductionDate(LocalDate.now().minusDays(1));
            session.setStartTime(LocalTime.of(8, 30));
            session.setEndTime(LocalTime.of(12, 0));
            session.setUnitsProduced(128);
            session.setHourlyLaborRate(new BigDecimal("17.25"));
            session.setOtherCost(new BigDecimal("24.00"));
            session.setNotes("Prep focused on beef and chicken batch for campus order.");

            ProductionSessionWorker sessionWorker1 = new ProductionSessionWorker();
            sessionWorker1.setSession(session);
            sessionWorker1.setWorker(worker1);
            sessionWorker1.setHoursWorked(new BigDecimal("3.50"));
            sessionWorker1.setHourlyRate(new BigDecimal("18.00"));
            sessionWorker1.setLaborCost(new BigDecimal("63.00"));

            ProductionSessionWorker sessionWorker2 = new ProductionSessionWorker();
            sessionWorker2.setSession(session);
            sessionWorker2.setWorker(worker2);
            sessionWorker2.setHoursWorked(new BigDecimal("3.50"));
            sessionWorker2.setHourlyRate(new BigDecimal("16.50"));
            sessionWorker2.setLaborCost(new BigDecimal("57.75"));

            session.getSessionWorkers().add(sessionWorker1);
            session.getSessionWorkers().add(sessionWorker2);

            productionSessionRepository.save(session);
        };
    }
}
