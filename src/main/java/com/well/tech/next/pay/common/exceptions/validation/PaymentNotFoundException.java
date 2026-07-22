package com.well.tech.next.pay.common.exceptions.validation;

import com.well.tech.next.pay.common.exceptions.BaseException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class PaymentNotFoundException extends BaseException {

    public PaymentNotFoundException(UUID paymentId) {
        super(
                String.format(
                        "Payment with id %s was not found",
                        paymentId
                ),
                HttpStatus.NOT_FOUND.value()
        );
    }
}