package com.well.tech.next.pay.common.exceptions.resource;

import com.well.tech.next.pay.common.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value());
    }
}