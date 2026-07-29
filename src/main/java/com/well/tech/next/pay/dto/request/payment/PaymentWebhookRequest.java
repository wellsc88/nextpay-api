package com.well.tech.next.pay.dto.request.payment;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
        name = "PaymentWebhookRequest",
        description = "Payload received from payment provider webhook events"
)
public record PaymentWebhookRequest(

        @Schema(
                description = "Payment UUID associated with the webhook event",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID paymentId,


        @Schema(
                description = "New payment status received from provider",
                example = "APPROVED"
        )
        PaymentStatus status,


        @Schema(
                description = "Unique identifier of the webhook event",
                example = "evt_123456789"
        )
        String eventId

) {
}