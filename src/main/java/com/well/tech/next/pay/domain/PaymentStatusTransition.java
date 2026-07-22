package com.well.tech.next.pay.domain;

import com.well.tech.next.pay.common.enums.PaymentStatus;

public final class PaymentStatusTransition {

    private PaymentStatusTransition() {}

    public static boolean isAllowed(
            PaymentStatus current,
            PaymentStatus target
    ) {
        return switch (current) {
            case PENDING ->
                    target == PaymentStatus.PROCESSING
                            || target == PaymentStatus.CANCELLED;

            case PROCESSING ->
                    target == PaymentStatus.APPROVED
                            || target == PaymentStatus.DECLINED;

            case APPROVED ->
                    target == PaymentStatus.REFUNDED;

            case DECLINED, CANCELLED, REFUNDED ->
                    false;
        };
    }
}