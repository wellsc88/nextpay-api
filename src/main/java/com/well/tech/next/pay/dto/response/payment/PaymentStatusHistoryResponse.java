package com.well.tech.next.pay.dto.response.payment;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        name = "PaymentStatusHistoryResponse",
        description = "Response payload containing payment status transition history"
)
public record PaymentStatusHistoryResponse(

        @Schema(
                description = "Status history record UUID",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,


        @Schema(
                description = "Previous payment status",
                example = "PENDING"
        )
        PaymentStatus fromStatus,


        @Schema(
                description = "New payment status",
                example = "APPROVED"
        )
        PaymentStatus toStatus,


        @Schema(
                description = "Date and time when the status changed",
                example = "2026-07-29T10:35:00"
        )
        LocalDateTime createdAt

) {
}