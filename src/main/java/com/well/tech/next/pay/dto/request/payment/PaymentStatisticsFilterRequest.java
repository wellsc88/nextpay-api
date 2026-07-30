package com.well.tech.next.pay.dto.request.payment;

import com.well.tech.next.pay.common.enums.PaymentMethod;
import com.well.tech.next.pay.common.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Filters for payment statistics")
public record PaymentStatisticsFilterRequest(

        @Schema(
                description = "Start date for the statistics period",
                example = "2026-07-01"
        )
        LocalDate from,

        @Schema(
                description = "End date for the statistics period",
                example = "2026-07-31"
        )
        LocalDate to,

        @Schema(
                description = "Filter by payment status",
                example = "APPROVED"
        )
        PaymentStatus status,

        @Schema(
                description = "Filter by payment method",
                example = "PIX"
        )
        PaymentMethod paymentMethod,

        @Schema(
                description = "Filter by payment currency",
                example = "BRL"
        )
        String currency,

        @Schema(
                description = "Filter by customer identifier",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID customerId

) {
}