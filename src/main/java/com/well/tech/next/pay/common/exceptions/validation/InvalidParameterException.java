package com.well.tech.next.pay.common.exceptions.validation;

import com.well.tech.next.pay.common.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidParameterException extends BaseException {

    public InvalidParameterException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
    }
}
