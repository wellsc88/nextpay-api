package com.well.tech.next.pay.dto.request.payment;

import com.well.tech.next.pay.common.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(
        name = "UpdatePaymentRequest",
        description = "Request payload for updating payment information"
)
public record UpdatePaymentRequest(

        @Schema(
                description = "Updated payment amount",
                example = "250.00"
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
                max = 3
        )
        String currency,


        @Schema(
                description = "Payment method",
                example = "PIX"
        )
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,


        @Schema(
                description = "Payment description",
                example = "Updated subscription payment"
        )
        @Size(max = 255)
        String description

) {
}