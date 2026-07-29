package com.well.tech.next.pay.dto.request.payment;

import com.well.tech.next.pay.common.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(
        name = "CreatePaymentRequest",
        description = "Request payload for creating a new payment"
)
public record CreatePaymentRequest(

        @Schema(
                description = "Customer UUID associated with the payment",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @NotNull(message = "Customer ID is required")
        UUID customerId,


        @Schema(
                description = "Payment amount",
                example = "150.00"
        )
        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Amount must be greater than zero"
        )
        @Digits(
                integer = 17,
                fraction = 2
        )
        BigDecimal amount,


        @Schema(
                description = "Payment currency code using ISO 4217 format",
                example = "BRL"
        )
        @NotBlank(message = "Currency is required")
        @Size(
                min = 3,
                max = 3,
                message = "Currency must have 3 characters"
        )
        String currency,


        @Schema(
                description = "Payment method",
                example = "CREDIT_CARD"
        )
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,


        @Schema(
                description = "Optional payment description",
                example = "Monthly subscription payment"
        )
        @Size(
                max = 255,
                message = "Description must have a maximum of 255 characters"
        )
        String description

) {
}