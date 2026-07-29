package com.well.tech.next.pay.common.exceptions.validation;

import com.well.tech.next.pay.common.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidPaymentRetryException extends BaseException {
    public InvalidPaymentRetryException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY.value());
    }
}
