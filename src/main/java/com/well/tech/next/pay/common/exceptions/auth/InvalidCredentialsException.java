package com.well.tech.next.pay.common.exceptions.auth;

import com.well.tech.next.pay.common.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BaseException {
    public InvalidCredentialsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED.value());
    }
}
