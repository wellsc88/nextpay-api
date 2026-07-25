package com.well.tech.next.pay.common.enums;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class PaymentStatusTransitions {

    private static final Map<PaymentStatus, Set<PaymentStatus>> TRANSITIONS =
            new EnumMap<>(PaymentStatus.class);

    static {
        TRANSITIONS.put(
                PaymentStatus.PENDING,
                EnumSet.of(
                        PaymentStatus.PROCESSING,
                        PaymentStatus.CANCELLED
                )
        );

        TRANSITIONS.put(
                PaymentStatus.PROCESSING,
                EnumSet.of(
                        PaymentStatus.APPROVED,
                        PaymentStatus.DECLINED
                )
        );

        TRANSITIONS.put(
                PaymentStatus.APPROVED,
                EnumSet.of(
                        PaymentStatus.REFUNDED
                )
        );

        TRANSITIONS.put(
                PaymentStatus.DECLINED,
                EnumSet.noneOf(PaymentStatus.class)
        );

        TRANSITIONS.put(
                PaymentStatus.CANCELLED,
                EnumSet.noneOf(PaymentStatus.class)
        );

        TRANSITIONS.put(
                PaymentStatus.REFUNDED,
                EnumSet.noneOf(PaymentStatus.class)
        );
    }

    private PaymentStatusTransitions() {
    }

    public static boolean isAllowed(
            PaymentStatus from,
            PaymentStatus to
    ) {
        return TRANSITIONS
                .getOrDefault(from, Set.of())
                .contains(to);
    }
}