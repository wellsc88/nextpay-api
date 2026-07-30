package com.well.tech.next.pay.dto.response.payment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "PaymentStatisticsResponse",
        description = "Response containing payment statistics"
)
public record PaymentStatisticsResponse(

        @Schema(
                description = "Total number of payments",
                example = "150"
        )
        long totalPayments,

        @Schema(
                description = "Total number of pending payments",
                example = "12"
        )
        long pendingPayments,

        @Schema(
                description = "Total number of processing payments",
                example = "8"
        )
        long processingPayments,

        @Schema(
                description = "Total number of approved payments",
                example = "100"
        )
        long approvedPayments,

        @Schema(
                description = "Total number of declined payments",
                example = "15"
        )
        long declinedPayments,

        @Schema(
                description = "Total number of cancelled payments",
                example = "7"
        )
        long cancelledPayments,

        @Schema(
                description = "Total number of refunded payments",
                example = "5"
        )
        long refundedPayments,

        @Schema(
                description = "Total number of expired payments",
                example = "3"
        )
        long expiredPayments,

        @Schema(
                description = "Total amount of all payments",
                example = "25890.50"
        )
        BigDecimal totalAmount,

        @Schema(
                description = "Total amount of approved payments",
                example = "22150.00"
        )
        BigDecimal approvedAmount,

        @Schema(
                description = "Total amount of refunded payments",
                example = "540.00"
        )
        BigDecimal refundedAmount

) {
}