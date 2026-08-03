package com.well.tech.next.pay.common.exceptions;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.well.tech.next.pay.common.exceptions.auth.EmailAlreadyExistsException;
import com.well.tech.next.pay.common.exceptions.auth.InvalidCredentialsException;
import com.well.tech.next.pay.common.exceptions.auth.UserDisabledException;
import com.well.tech.next.pay.common.exceptions.auth.UserNotFoundException;
import com.well.tech.next.pay.common.exceptions.validation.CustomerNotFoundException;
import com.well.tech.next.pay.common.exceptions.validation.InvalidPaymentRetryException;
import com.well.tech.next.pay.common.exceptions.validation.InvalidPaymentStatusTransitionException;
import com.well.tech.next.pay.common.exceptions.validation.PaymentExpiredException;
import com.well.tech.next.pay.common.exceptions.validation.PaymentNotFoundException;
import com.well.tech.next.pay.common.exceptions.validation.InvalidParameterException;
import com.well.tech.next.pay.common.exceptions.validation.InvalidWebhookSignatureException;
import com.well.tech.next.pay.common.exceptions.validation.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    private HttpServletRequest request;

    private static class TestBaseException extends BaseException {

        public TestBaseException(String message, int status) {
            super(message, status);
        }
    }

    @BeforeEach
    void setUp() {

        handler = new GlobalExceptionHandler();

        request = mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/v1/customers");
    }

    @Test
    void shouldHandleBaseException() {

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/users");

        BaseException exception =
                new TestBaseException(
                        "User not found",
                        404
                );

        ResponseEntity<ApiError> response =
                handler.handleBaseException(
                        exception,
                        request
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(response.getBody())
                .isNotNull();

        assertThat(response.getBody().message())
                .isEqualTo("User not found");

        verify(request)
                .getRequestURI();
    }

    @Test
    void shouldReturnInternalServerErrorWhenBaseExceptionHasInvalidStatus() {

        BaseException exception =
                new TestBaseException(
                        "Invalid status",
                        999
                );

        ResponseEntity<ApiError> response =
                handler.handleBaseException(
                        exception,
                        request
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        assertThat(response.getBody())
                .isNotNull();

        assertThat(response.getBody().message())
                .isEqualTo("Invalid status");
    }

    @Test
    void shouldHandleValidationException() {

        FieldError fieldError =
                new FieldError(
                        "customer",
                        "email",
                        "must not be blank"
                );

        BindingResult bindingResult =
                mock(BindingResult.class);

        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult())
                .thenReturn(bindingResult);

        ResponseEntity<ApiError> response =
                handler.handleValidationException(
                        exception,
                        request
                );

        assertThat(response.getStatusCode())
               .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response.getBody())
                .isNotNull();

        assertThat(response.getBody().message())
                .isEqualTo(
                        "email: must not be blank"
                );
    }

    @Test
    void shouldHandleValidationTypeMismatch() {

        FieldError fieldError =
                new FieldError(
                        "customer",
                        "age",
                        null,
                        false,
                        new String[]{"typeMismatch"},
                        null,
                        "Invalid value"
                );

        BindingResult bindingResult =
                mock(BindingResult.class);

        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult())
                .thenReturn(bindingResult);

        ResponseEntity<ApiError> response =
                handler.handleValidationException(
                        exception,
                        request
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response.getBody())
                .isNotNull();

        assertThat(response.getBody().message())
                .isEqualTo(
                        "Invalid value for parameter 'age'"
                );
    }

    @Test
    void shouldReturnDefaultValidationMessageWhenNoFieldErrors() {

        BindingResult bindingResult =
                mock(BindingResult.class);

        when(bindingResult.getFieldErrors())
                .thenReturn(List.of());

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult())
                .thenReturn(bindingResult);

        ResponseEntity<ApiError> response =
                handler.handleValidationException(
                        exception,
                        request
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response.getBody())
                .isNotNull();

        assertThat(response.getBody().message())
                .isEqualTo(
                        "Invalid request"
                );
    }

    @Test
    void shouldHandleTypeMismatchException() {

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/users/test");

        MethodArgumentTypeMismatchException exception =
                new MethodArgumentTypeMismatchException(
                        "abc",
                        Long.class,
                        "id",
                        null,
                        new IllegalArgumentException()
                );

        ResponseEntity<ApiError> response =
                handler.handleTypeMismatch(
                        exception,
                        request
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response.getBody())
                .isNotNull();

        assertThat(response.getBody().message())
                .isEqualTo(
                        "Invalid value 'abc' for parameter 'id'"
                );
    }

    @Test
    void shouldHandleGenericException() {

        Exception exception =
                new RuntimeException("Database down");

        ResponseEntity<ApiError> response =
                handler.handleGenericException(
                        exception,
                        request
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        assertThat(response.getBody())
                .isNotNull();

        assertThat(response.getBody().message())
                .isEqualTo(
                        "Unexpected error occurred"
                );
    }

    @Test
    void shouldCreateResourceNotFoundException() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException(
                        "Customer not found"
                );

        assertThat(exception.getMessage())
                .isEqualTo("Customer not found");

        assertThat(exception.getStatus())
                .isEqualTo(404);
    }

    @Test
    void shouldCreateEmailAlreadyExistsException() {

        EmailAlreadyExistsException exception =
                new EmailAlreadyExistsException("Email already exists");

        assertThat(exception.getMessage())
                .isEqualTo("Email already exists");

        assertThat(exception.getStatus())
                .isEqualTo(409);
    }

    @Test
    void shouldCreateInvalidCredentialsException() {

        InvalidCredentialsException exception =
                new InvalidCredentialsException("Invalid credentials");

        assertThat(exception.getStatus())
                .isEqualTo(401);
    }

    @Test
    void shouldCreateUserDisabledException() {

        UserDisabledException exception =
                new UserDisabledException("User disabled");

        assertThat(exception.getStatus())
                .isEqualTo(403);
    }

    @Test
    void shouldCreateUserNotFoundException() {

        UserNotFoundException exception =
                new UserNotFoundException("User not found");

        assertThat(exception.getStatus())
                .isEqualTo(404);
    }

    @Test
    void shouldCreateCustomerNotFoundException() {

        CustomerNotFoundException exception =
                new CustomerNotFoundException("Customer not found");

        assertThat(exception.getStatus())
                .isEqualTo(404);
    }

    @Test
    void shouldCreateInvalidParameterException() {

        InvalidParameterException exception =
                new InvalidParameterException("Invalid parameter");

        assertThat(exception.getStatus())
                .isEqualTo(400);
    }

    @Test
    void shouldCreateInvalidPaymentRetryException() {

        InvalidPaymentRetryException exception =
                new InvalidPaymentRetryException("Invalid retry");

        assertThat(exception.getStatus())
                .isEqualTo(422);
    }

    @Test
    void shouldCreateInvalidPaymentStatusTransitionException() {

        PaymentStatus currentStatus = PaymentStatus.APPROVED;
        PaymentStatus newStatus = PaymentStatus.CANCELLED;

        InvalidPaymentStatusTransitionException exception =
                new InvalidPaymentStatusTransitionException(
                        currentStatus,
                        newStatus
                );

        assertThat(exception.getStatus())
                .isEqualTo(422);
    }

    @Test
    void shouldCreateInvalidWebhookSignatureException() {

        InvalidWebhookSignatureException exception =
                new InvalidWebhookSignatureException(
                        "Invalid signature"
                );

        assertThat(exception.getStatus())
                .isEqualTo(401);
    }

    @Test
    void shouldCreatePaymentExpiredException() {

        PaymentExpiredException exception =
                new PaymentExpiredException(
                        "Payment expired"
                );

        assertThat(exception.getStatus())
                .isEqualTo(422);
    }

    @Test
    void shouldCreatePaymentNotFoundException() {

        PaymentNotFoundException exception =
                new PaymentNotFoundException(
                        UUID.randomUUID()
                );

        assertThat(exception.getStatus())
                .isEqualTo(404);
    }

    @Test
    void shouldCreateValidationException() {

        ValidationException exception =
                new ValidationException(
                        "Validation error"
                );


        assertThat(exception.getStatus())
                .isEqualTo(400);
    }
}