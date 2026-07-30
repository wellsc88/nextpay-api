package com.well.tech.next.pay.dto.response.payment;

import com.well.tech.next.pay.common.enums.PaymentMethod;
import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.entity.Payment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        name = "PaymentResponse",
        description = "Response payload containing payment details"
)
public record PaymentResponse(

        @Schema(
                description = "Payment UUID",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,


        @Schema(
                description = "Customer UUID associated with the payment",
                example = "550e8400-e29b-41d4-a716-446655440001"
        )
        UUID customerId,


        @Schema(
                description = "Payment amount",
                example = "150.00"
        )
        BigDecimal amount,


        @Schema(
                description = "Payment currency code",
                example = "BRL"
        )
        String currency,


        @Schema(
                description = "Current payment status",
                example = "APPROVED"
        )
        PaymentStatus status,


        @Schema(
                description = "Payment method used",
                example = "PIX"
        )
        PaymentMethod paymentMethod,


        @Schema(
                description = "Payment description",
                example = "Monthly subscription payment"
        )
        String description,

        @Schema(
                description = "Payment reference",
                example = "PAY-20260730-8F3A1C"
        )
        String reference,

        @Schema(
                description = "Payment creation date/time",
                example = "2026-07-29T10:30:00"
        )
        LocalDateTime createdAt,


        @Schema(
                description = "Payment last update date/time",
                example = "2026-07-29T10:35:00"
        )
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
                payment.getReference(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}