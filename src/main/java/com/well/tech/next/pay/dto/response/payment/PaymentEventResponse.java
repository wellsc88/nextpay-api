package com.well.tech.next.pay.dto.response.payment;

import com.well.tech.next.pay.common.enums.PaymentEventType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        name = "PaymentEventResponse",
        description = "Response containing a payment event record"
)
public record PaymentEventResponse(

        @Schema(
                description = "Payment event unique identifier",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "Type of payment event",
                example = "PAYMENT_CREATED"
        )
        PaymentEventType eventType,

        @Schema(
                description = "Human-readable description of the event",
                example = "Payment created successfully"
        )
        String description,

        @Schema(
                description = "Date and time when the event occurred",
                example = "2026-07-30T10:15:30"
        )
        LocalDateTime createdAt

) {
}