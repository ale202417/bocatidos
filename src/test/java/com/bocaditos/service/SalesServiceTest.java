package com.bocaditos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bocaditos.domain.partner.Partner;
import com.bocaditos.domain.partner.PartnerStatus;
import com.bocaditos.domain.sales.PaymentStatus;
import com.bocaditos.domain.sales.SaleOrder;
import com.bocaditos.dto.sales.SaleOrderForm;
import com.bocaditos.dto.sales.SaleOrderFormItem;
import com.bocaditos.repository.CustomerRepository;
import com.bocaditos.repository.PartnerRepository;
import com.bocaditos.repository.SaleOrderRepository;
import com.bocaditos.service.sales.SalesService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
class SalesServiceTest {

    @Mock
    private SaleOrderRepository saleOrderRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private SalesService salesService;

    @Captor
    private ArgumentCaptor<SaleOrder> orderCaptor;

    @Test
    void createOrderComputesTotalsAndPartialStatus() {
        Partner partner = new Partner();
        partner.setFirstName("Rosa");
        partner.setLastName("Martinez");
        partner.setStatus(PartnerStatus.ACTIVE);

        when(partnerRepository.findById(1L)).thenReturn(Optional.of(partner));
        when(saleOrderRepository.save(any(SaleOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SaleOrderForm form = new SaleOrderForm();
        form.setOrderNumber("SO-2001");
        form.setOrderDate(LocalDate.of(2026, 3, 27));
        form.setPartnerId(1L);
        form.setPaymentStatus(PaymentStatus.UNPAID);
        form.setDiscountAmount(new BigDecimal("10.00"));
        form.setAmountPaid(new BigDecimal("20.00"));

        SaleOrderFormItem item1 = new SaleOrderFormItem();
        item1.setItemName("Chicken");
        item1.setQuantity(10);
        item1.setUnitPrice(new BigDecimal("2.50"));

        SaleOrderFormItem item2 = new SaleOrderFormItem();
        item2.setItemName("Beef");
        item2.setQuantity(5);
        item2.setUnitPrice(new BigDecimal("3.00"));

        form.setItems(List.of(item1, item2));

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "saleOrderForm");
        SaleOrder saved = salesService.createOrder(form, bindingResult);

        assertThat(bindingResult.hasErrors()).isFalse();
        assertThat(saved).isNotNull();
        assertThat(saved.getSubtotal()).isEqualByComparingTo("40.00");
        assertThat(saved.getTotalAmount()).isEqualByComparingTo("30.00");
        assertThat(saved.getAmountPaid()).isEqualByComparingTo("20.00");
        assertThat(saved.getPaymentStatus()).isEqualTo(PaymentStatus.PARTIALLY_PAID);
        assertThat(saved.getItems()).hasSize(2);
    }
}
