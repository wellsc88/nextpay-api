package com.well.tech.next.pay.common.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError> handleBaseException(
            BaseException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.resolve(ex.getStatus());

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String path = request.getRequestURI();

        log.warn(
                "Business exception. Type={}, Status={}, Path={}, Message={}",
                ex.getClass().getSimpleName(),
                status.value(),
                path,
                ex.getMessage()
        );

        ApiError error = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                path,
                Instant.now()
        );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String path = request.getRequestURI();

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {

                    if ("typeMismatch".equals(error.getCode())) {
                        return "Invalid value for parameter '"
                                + error.getField() + "'";
                    }

                    return error.getField()
                            + ": "
                            + error.getDefaultMessage();
                })
                .findFirst()
                .orElse("Invalid request");

        log.warn(
                "Validation exception. Path={}, Message={}",
                path,
                message
        );

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                path,
                Instant.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String path = request.getRequestURI();

        String message = String.format(
                "Invalid value '%s' for parameter '%s'",
                ex.getValue(),
                ex.getName()
        );

        log.warn(
                "Type mismatch exception. Path={}, Parameter={}, Value={}",
                path,
                ex.getName(),
                ex.getValue()
        );

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                path,
                Instant.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        String path = request.getRequestURI();

        log.error(
                "Unexpected exception. Type={}, Path={}",
                ex.getClass().getSimpleName(),
                path,
                ex
        );

        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Unexpected error occurred",
                path,
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}