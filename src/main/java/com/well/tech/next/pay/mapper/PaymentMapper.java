package com.well.tech.next.pay.mapper;

import com.well.tech.next.pay.entity.Customer;
import com.well.tech.next.pay.dto.request.payment.CreatePaymentRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentResponse;
import com.well.tech.next.pay.dto.request.payment.UpdatePaymentRequest;
import com.well.tech.next.pay.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(
            CreatePaymentRequest request,
            Customer customer
    ) {
        return Payment.builder()
                .customer(customer)
                .amount(request.amount())
                .currency(request.currency().toUpperCase())
                .paymentMethod(request.paymentMethod())
                .description(request.description())
                .build();
    }

    public void updateEntity(
            Payment payment,
            UpdatePaymentRequest request
    ) {
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency().toUpperCase());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setDescription(request.description());
    }

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getCustomer().getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getPaymentMethod(),
                payment.getDescription(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}