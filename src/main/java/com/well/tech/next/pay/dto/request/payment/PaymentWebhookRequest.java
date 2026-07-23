package com.well.tech.next.pay.dto.request.payment;

import com.well.tech.next.pay.common.enums.PaymentStatus;

import java.util.UUID;

public record PaymentWebhookRequest(
        UUID paymentId,
        PaymentStatus status,
        String eventId
) {
}