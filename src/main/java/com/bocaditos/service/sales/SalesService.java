package com.bocaditos.service.sales;

import com.bocaditos.domain.customer.Customer;
import com.bocaditos.domain.partner.Partner;
import com.bocaditos.domain.sales.PaymentRecord;
import com.bocaditos.domain.sales.PaymentStatus;
import com.bocaditos.domain.sales.SaleOrder;
import com.bocaditos.domain.sales.SaleOrderItem;
import com.bocaditos.dto.sales.SaleOrderDetailItemView;
import com.bocaditos.dto.sales.SaleOrderDetailView;
import com.bocaditos.dto.sales.SaleOrderForm;
import com.bocaditos.dto.sales.SaleOrderFormItem;
import com.bocaditos.dto.sales.SaleOrderListItem;
import com.bocaditos.dto.sales.SalesFormOptions;
import com.bocaditos.repository.CustomerRepository;
import com.bocaditos.repository.PartnerRepository;
import com.bocaditos.repository.SaleOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

@Service
@Transactional(readOnly = true)
public class SalesService {

    private static final int DEFAULT_ITEM_ROWS = 3;

    private final SaleOrderRepository saleOrderRepository;
    private final PartnerRepository partnerRepository;
    private final CustomerRepository customerRepository;

    public SalesService(
            SaleOrderRepository saleOrderRepository,
            PartnerRepository partnerRepository,
            CustomerRepository customerRepository
    ) {
        this.saleOrderRepository = saleOrderRepository;
        this.partnerRepository = partnerRepository;
        this.customerRepository = customerRepository;
    }

    public List<SaleOrderListItem> getOrders(String query) {
        List<SaleOrder> orders = StringUtils.hasText(query)
                ? saleOrderRepository.searchForList(query.trim())
                : saleOrderRepository.findAllForList();

        return orders.stream()
                .map(order -> new SaleOrderListItem(
                        order.getId(),
                        order.getOrderNumber(),
                        order.getOrderDate(),
                        order.getPartner().getFirstName() + " " + order.getPartner().getLastName(),
                        order.getCustomer() != null ? order.getCustomer().getFullName() : "Walk-in / direct sale",
                        order.getItems().size(),
                        order.getPaymentStatus(),
                        order.getTotalAmount(),
                        order.getAmountPaid()
                ))
                .toList();
    }

    public SaleOrderDetailView getOrder(Long id) {
        SaleOrder order = saleOrderRepository.findDetailedById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale order not found"));

        return new SaleOrderDetailView(
                order.getId(),
                order.getOrderNumber(),
                order.getOrderDate(),
                order.getPartner().getFirstName() + " " + order.getPartner().getLastName(),
                order.getCustomer() != null ? order.getCustomer().getFullName() : "Walk-in / direct sale",
                order.getPaymentStatus(),
                order.getSubtotal(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                order.getAmountPaid(),
                order.getTotalAmount().subtract(order.getAmountPaid()),
                order.getNotes(),
                order.getItems().stream()
                        .map(item -> new SaleOrderDetailItemView(
                                item.getItemName(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getLineTotal(),
                                item.getNotes()
                        ))
                        .toList()
        );
    }

    public SaleOrderForm emptyForm() {
        SaleOrderForm form = new SaleOrderForm();
        form.setItems(defaultEmptyItems());
        return form;
    }

    public SalesFormOptions getFormOptions() {
        return new SalesFormOptions(
                partnerRepository.findAllByOrderByLastNameAscFirstNameAsc().stream()
                        .map(partner -> new SalesFormOptions.ReferenceOption(
                                partner.getId(),
                                partner.getFirstName() + " " + partner.getLastName()
                        ))
                        .toList(),
                customerRepository.findAllByOrderByFullNameAsc().stream()
                        .map(customer -> new SalesFormOptions.ReferenceOption(customer.getId(), customer.getFullName()))
                        .toList()
        );
    }

    @Transactional
    public SaleOrder createOrder(SaleOrderForm form, BindingResult bindingResult) {
        List<SaleOrderFormItem> validItems = validItems(form.getItems());
        if (validItems.isEmpty()) {
            bindingResult.reject("items.required", "Add at least one valid line item before saving the order.");
            return null;
        }

        BigDecimal subtotal = validItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal discount = defaultIfNull(form.getDiscountAmount());
        BigDecimal total = subtotal.subtract(discount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        BigDecimal amountPaid = defaultIfNull(form.getAmountPaid()).setScale(2, RoundingMode.HALF_UP);

        if (amountPaid.compareTo(total) > 0) {
            bindingResult.rejectValue("amountPaid", "amountPaid.exceedsTotal", "Amount paid cannot exceed the order total.");
            return null;
        }

        PaymentStatus resolvedStatus = resolveStatus(amountPaid, total, form.getPaymentStatus());

        Partner partner = partnerRepository.findById(form.getPartnerId())
                .orElseThrow(() -> new EntityNotFoundException("Partner not found"));
        Customer customer = form.getCustomerId() != null
                ? customerRepository.findById(form.getCustomerId()).orElseThrow(() -> new EntityNotFoundException("Customer not found"))
                : null;

        SaleOrder order = new SaleOrder();
        order.setOrderNumber(form.getOrderNumber().trim());
        order.setOrderDate(form.getOrderDate());
        order.setPartner(partner);
        order.setCustomer(customer);
        order.setPaymentStatus(resolvedStatus);
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discount);
        order.setTotalAmount(total);
        order.setAmountPaid(amountPaid);
        order.setNotes(form.getNotes());

        for (SaleOrderFormItem formItem : validItems) {
            SaleOrderItem item = new SaleOrderItem();
            item.setSaleOrder(order);
            item.setItemName(formItem.getItemName().trim());
            item.setQuantity(formItem.getQuantity());
            item.setUnitPrice(formItem.getUnitPrice().setScale(2, RoundingMode.HALF_UP));
            item.setLineTotal(formItem.getUnitPrice().multiply(BigDecimal.valueOf(formItem.getQuantity())).setScale(2, RoundingMode.HALF_UP));
            item.setNotes(formItem.getNotes());
            order.getItems().add(item);
        }

        if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setSaleOrder(order);
            paymentRecord.setPaymentDate(form.getOrderDate());
            paymentRecord.setAmount(amountPaid);
            paymentRecord.setPaymentMethod(form.getPaymentMethod());
            paymentRecord.setNotes("Initial payment recorded at order entry.");
            order.getPayments().add(paymentRecord);
        }

        return saleOrderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!saleOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("Sale order not found");
        }
        saleOrderRepository.deleteById(id);
    }

    private List<SaleOrderFormItem> defaultEmptyItems() {
        List<SaleOrderFormItem> items = new ArrayList<>();
        for (int i = 0; i < DEFAULT_ITEM_ROWS; i++) {
            items.add(new SaleOrderFormItem());
        }
        return items;
    }

    private List<SaleOrderFormItem> validItems(List<SaleOrderFormItem> items) {
        return items.stream()
                .filter(item -> StringUtils.hasText(item.getItemName())
                        && item.getQuantity() != null
                        && item.getQuantity() > 0
                        && item.getUnitPrice() != null
                        && item.getUnitPrice().compareTo(BigDecimal.ZERO) >= 0)
                .toList();
    }

    private BigDecimal defaultIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private PaymentStatus resolveStatus(BigDecimal amountPaid, BigDecimal total, PaymentStatus requestedStatus) {
        if (total.compareTo(BigDecimal.ZERO) == 0 || amountPaid.compareTo(total) == 0) {
            return PaymentStatus.PAID;
        }
        if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
            return PaymentStatus.PARTIALLY_PAID;
        }
        return requestedStatus != null ? requestedStatus : PaymentStatus.UNPAID;
    }
}
