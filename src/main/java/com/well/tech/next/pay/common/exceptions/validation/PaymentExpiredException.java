package com.well.tech.next.pay.common.exceptions.validation;

import com.well.tech.next.pay.common.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class PaymentExpiredException extends BaseException {
    public PaymentExpiredException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY.value());
    }
}
