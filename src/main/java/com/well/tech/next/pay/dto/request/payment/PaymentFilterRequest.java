package com.well.tech.next.pay.dto.request.payment;

import com.well.tech.next.pay.common.enums.PaymentMethod;
import com.well.tech.next.pay.common.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        name = "PaymentFilterRequest",
        description = "Filters available for searching payments"
)
public record PaymentFilterRequest(

        @Schema(
                description = "Filter payments by status",
                example = "APPROVED"
        )
        PaymentStatus status,

        @Schema(
                description = "Filter payments by payment method",
                example = "PIX"
        )
        PaymentMethod paymentMethod,

        @Schema(
                description = "Filter payments by currency code",
                example = "BRL"
        )
        String currency,

        @Schema(
                description = "Filter payments by customer UUID",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID customerId,

        @Schema(
                description = "Filter payments by reference",
                example = "PAY-20260730-ABC123"
        )
        String reference,

        @Schema(
                description = "Filter retry payments by parent payment UUID",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID parentPaymentId,

        @Schema(
                description = "Minimum payment amount",
                example = "10.00"
        )
        BigDecimal minAmount,

        @Schema(
                description = "Maximum payment amount",
                example = "1000.00"
        )
        BigDecimal maxAmount,

        @Schema(
                description = "Filter payments created after this date/time",
                example = "2026-01-01T00:00:00"
        )
        LocalDateTime createdAtFrom,

        @Schema(
                description = "Filter payments created before this date/time",
                example = "2026-12-31T23:59:59"
        )
        LocalDateTime createdAtTo,

        @Schema(
                description = "Filter payments updated after this date/time",
                example = "2026-01-01T00:00:00"
        )
        LocalDateTime updatedAtFrom,

        @Schema(
                description = "Filter payments updated before this date/time",
                example = "2026-12-31T23:59:59"
        )
        LocalDateTime updatedAtTo,

        @Schema(
                description = "Filter payments expiring after this date/time",
                example = "2026-01-01T00:00:00"
        )
        LocalDateTime expiresAtFrom,

        @Schema(
                description = "Filter payments expiring before this date/time",
                example = "2026-12-31T23:59:59"
        )
        LocalDateTime expiresAtTo

) {
}