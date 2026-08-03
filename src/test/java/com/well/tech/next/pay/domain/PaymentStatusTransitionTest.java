package com.well.tech.next.pay.domain;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentStatusTransitionTest {


    @Test
    void shouldAllowValidTransitions() {

        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.PENDING,
                        PaymentStatus.PROCESSING
                )
        ).isTrue();


        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.PENDING,
                        PaymentStatus.CANCELLED
                )
        ).isTrue();


        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.PENDING,
                        PaymentStatus.EXPIRED
                )
        ).isTrue();


        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.PROCESSING,
                        PaymentStatus.APPROVED
                )
        ).isTrue();


        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.PROCESSING,
                        PaymentStatus.DECLINED
                )
        ).isTrue();


        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.APPROVED,
                        PaymentStatus.REFUNDED
                )
        ).isTrue();
    }


    @Test
    void shouldRejectInvalidPendingTransitions() {

        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.PENDING,
                        PaymentStatus.APPROVED
                )
        ).isFalse();


        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.PENDING,
                        PaymentStatus.DECLINED
                )
        ).isFalse();
    }


    @Test
    void shouldRejectInvalidProcessingTransitions() {

        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.PROCESSING,
                        PaymentStatus.CANCELLED
                )
        ).isFalse();


        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.PROCESSING,
                        PaymentStatus.EXPIRED
                )
        ).isFalse();
    }


    @Test
    void shouldRejectInvalidApprovedTransitions() {

        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.APPROVED,
                        PaymentStatus.DECLINED
                )
        ).isFalse();
    }


    @Test
    void shouldRejectFinalStateTransitions() {

        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.DECLINED,
                        PaymentStatus.APPROVED
                )
        ).isFalse();


        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.CANCELLED,
                        PaymentStatus.PROCESSING
                )
        ).isFalse();


        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.REFUNDED,
                        PaymentStatus.APPROVED
                )
        ).isFalse();


        assertThat(
                PaymentStatusTransition.isAllowed(
                        PaymentStatus.EXPIRED,
                        PaymentStatus.PROCESSING
                )
        ).isFalse();
    }
}