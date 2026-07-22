package com.well.tech.next.pay.dto.request.payment;

import com.well.tech.next.pay.common.enums.PaymentStatus;

public record UpdatePaymentStatusRequest(
        PaymentStatus status
) {
}
