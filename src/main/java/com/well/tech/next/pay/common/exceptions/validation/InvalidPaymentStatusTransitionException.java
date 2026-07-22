package com.well.tech.next.pay.common.exceptions.validation;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidPaymentStatusTransitionException
        extends BaseException {

    public InvalidPaymentStatusTransitionException(
            PaymentStatus currentStatus,
            PaymentStatus targetStatus
    ) {
        super(
                String.format(
                        "Invalid payment status transition from %s to %s",
                        currentStatus,
                        targetStatus
                ),
                HttpStatus.UNPROCESSABLE_ENTITY.value()
        );
    }
}