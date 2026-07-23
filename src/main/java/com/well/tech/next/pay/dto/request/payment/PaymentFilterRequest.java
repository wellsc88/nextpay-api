package com.well.tech.next.pay.dto.request.payment;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentFilterRequest(
        PaymentStatus status,
        PaymentMethod paymentMethod,
        String currency,
        UUID customerId,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        LocalDateTime createdAtFrom,
        LocalDateTime createdAtTo
) {
}