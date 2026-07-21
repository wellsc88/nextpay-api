package com.well.tech.next.pay.dto.request.payment;

import com.well.tech.next.pay.common.enums.PaymentMethod;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdatePaymentRequest(

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        @Digits(integer = 17, fraction = 2)
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3)
        String currency,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @Size(max = 255)
        String description
) {
}