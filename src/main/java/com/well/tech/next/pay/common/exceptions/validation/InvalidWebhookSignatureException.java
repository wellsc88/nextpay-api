package com.well.tech.next.pay.common.exceptions.validation;

import com.well.tech.next.pay.common.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidWebhookSignatureException extends BaseException {
    public InvalidWebhookSignatureException(String message) {
        super(message, HttpStatus.UNAUTHORIZED.value());
    }
}