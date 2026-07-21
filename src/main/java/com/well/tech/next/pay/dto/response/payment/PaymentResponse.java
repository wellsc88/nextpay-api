package com.well.tech.next.pay.dto.response.payment;

import com.well.tech.next.pay.common.enums.PaymentMethod;
import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID customerId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        PaymentMethod paymentMethod,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PaymentResponse from(Payment payment) {
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