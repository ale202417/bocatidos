package com.bocaditos.dto.sales;

import com.bocaditos.domain.sales.PaymentMethod;
import com.bocaditos.domain.sales.PaymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleOrderForm {

    @NotBlank
    @Size(max = 30)
    private String orderNumber;

    @NotNull
    private LocalDate orderDate = LocalDate.now();

    @NotNull
    private Long partnerId;

    private Long customerId;

    @NotNull
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal amountPaid = BigDecimal.ZERO;

    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    @Size(max = 500)
    private String notes;

    @Valid
    private List<SaleOrderFormItem> items = new ArrayList<>();

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<SaleOrderFormItem> getItems() {
        return items;
    }

    public void setItems(List<SaleOrderFormItem> items) {
        this.items = items;
    }
}
