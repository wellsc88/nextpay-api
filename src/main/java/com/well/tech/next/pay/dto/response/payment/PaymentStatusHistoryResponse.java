package com.well.tech.next.pay.dto.response.payment;

import com.well.tech.next.pay.common.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentStatusHistoryResponse(
        UUID id,
        PaymentStatus fromStatus,
        PaymentStatus toStatus,
        LocalDateTime createdAt
) {
}
