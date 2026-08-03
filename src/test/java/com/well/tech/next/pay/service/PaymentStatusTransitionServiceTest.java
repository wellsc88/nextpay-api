package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.validation.InvalidPaymentStatusTransitionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentStatusTransitionServiceTest {

    private final PaymentStatusTransitionService service =
            new PaymentStatusTransitionService();

    @Test
    void shouldAllowSameStatus() {

        assertDoesNotThrow(() ->
                service.validate(
                        PaymentStatus.PENDING,
                        PaymentStatus.PENDING
                )
        );
    }

    @Test
    void shouldAllowPendingTransitions() {

        assertDoesNotThrow(() ->
                service.validate(
                        PaymentStatus.PENDING,
                        PaymentStatus.PROCESSING
                )
        );

        assertDoesNotThrow(() ->
                service.validate(
                        PaymentStatus.PENDING,
                        PaymentStatus.CANCELLED
                )
        );

        assertDoesNotThrow(() ->
                service.validate(
                        PaymentStatus.PENDING,
                        PaymentStatus.EXPIRED
                )
        );
    }

    @Test
    void shouldAllowProcessingTransitions() {

        assertDoesNotThrow(() ->
                service.validate(
                        PaymentStatus.PROCESSING,
                        PaymentStatus.APPROVED
                )
        );

        assertDoesNotThrow(() ->
                service.validate(
                        PaymentStatus.PROCESSING,
                        PaymentStatus.DECLINED
                )
        );

        assertDoesNotThrow(() ->
                service.validate(
                        PaymentStatus.PROCESSING,
                        PaymentStatus.CANCELLED
                )
        );
    }

    @Test
    void shouldAllowApprovedToRefunded() {

        assertDoesNotThrow(() ->
                service.validate(
                        PaymentStatus.APPROVED,
                        PaymentStatus.REFUNDED
                )
        );
    }

    @Test
    void shouldRejectInvalidTransitions() {

        assertThrows(
                InvalidPaymentStatusTransitionException.class,
                () ->
                        service.validate(
                                PaymentStatus.PENDING,
                                PaymentStatus.APPROVED
                        )
        );

        assertThrows(
                InvalidPaymentStatusTransitionException.class,
                () ->
                        service.validate(
                                PaymentStatus.APPROVED,
                                PaymentStatus.DECLINED
                        )
        );

        assertThrows(
                InvalidPaymentStatusTransitionException.class,
                () ->
                        service.validate(
                                PaymentStatus.DECLINED,
                                PaymentStatus.APPROVED
                        )
        );

        assertThrows(
                InvalidPaymentStatusTransitionException.class,
                () ->
                        service.validate(
                                PaymentStatus.CANCELLED,
                                PaymentStatus.PROCESSING
                        )
        );

        assertThrows(
                InvalidPaymentStatusTransitionException.class,
                () ->
                        service.validate(
                                PaymentStatus.REFUNDED,
                                PaymentStatus.APPROVED
                        )
        );

        assertThrows(
                InvalidPaymentStatusTransitionException.class,
                () ->
                        service.validate(
                                PaymentStatus.EXPIRED,
                                PaymentStatus.APPROVED
                        )
        );
    }

    @Test
    void shouldAllowProcessingToCancelled() {

        assertDoesNotThrow(() ->
                service.validate(
                        PaymentStatus.PROCESSING,
                        PaymentStatus.CANCELLED
                )
        );
    }

    @Test
    void shouldRejectProcessingToPending() {

        assertThrows(
                InvalidPaymentStatusTransitionException.class,
                () ->
                        service.validate(
                                PaymentStatus.PROCESSING,
                                PaymentStatus.PENDING
                        )
        );
    }
}