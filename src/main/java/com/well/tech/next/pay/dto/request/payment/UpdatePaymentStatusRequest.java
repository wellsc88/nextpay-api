package com.well.tech.next.pay.dto.request.payment;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "UpdatePaymentStatusRequest",
        description = "Request payload for updating payment status"
)
public record UpdatePaymentStatusRequest(

        @Schema(
                description = "New payment status",
                example = "APPROVED"
        )
        PaymentStatus status

) {
}