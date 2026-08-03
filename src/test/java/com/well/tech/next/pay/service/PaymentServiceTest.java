package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentEventType;
import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.common.exceptions.validation.CustomerNotFoundException;
import com.well.tech.next.pay.common.exceptions.validation.InvalidPaymentRetryException;
import com.well.tech.next.pay.common.exceptions.validation.InvalidPaymentStatusTransitionException;
import com.well.tech.next.pay.common.exceptions.validation.PaymentExpiredException;
import com.well.tech.next.pay.dto.request.payment.CreatePaymentRequest;
import com.well.tech.next.pay.entity.Customer;
import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.mapper.PaymentMapper;
import com.well.tech.next.pay.repository.CustomerRepository;
import com.well.tech.next.pay.repository.PaymentRepository;
import com.well.tech.next.pay.dto.response.payment.PaymentResponse;
import com.well.tech.next.pay.dto.request.payment.PaymentFilterRequest;
import com.well.tech.next.pay.dto.request.payment.UpdatePaymentRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentStatusHistoryService paymentStatusHistoryService;

    @Mock
    private PaymentStatusTransitionService paymentStatusTransitionService;

    @Mock
    private PaymentEventService paymentEventService;

    @Mock
    private PaymentReferenceService paymentReferenceService;

    @InjectMocks
    private PaymentService paymentService;

    private UUID paymentId;
    private Payment payment;
    private PaymentResponse response;

    @BeforeEach
    void setup(){

        paymentId = UUID.randomUUID();

        payment = Payment.builder()
                .id(paymentId)
                .status(PaymentStatus.PENDING)
                .build();
    }

    @Test
    void shouldFindPaymentByIdSuccessfully(){

        PaymentResponse response =
                mock(PaymentResponse.class);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentMapper.toResponse(payment))
                .thenReturn(response);

        PaymentResponse result =
                paymentService.findById(paymentId);

        assertNotNull(result);

        verify(paymentRepository)
                .findById(paymentId);
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFound(){

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        paymentService.findById(paymentId)
        );

        verify(paymentRepository)
                .findById(paymentId);
    }

    @Test
    void shouldDeletePaymentSuccessfully(){

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        paymentService.delete(paymentId);

        verify(paymentRepository)
                .delete(payment);
    }

    @Test
    void shouldFindAllPaymentsSuccessfully() {

        PaymentFilterRequest filter =
                mock(PaymentFilterRequest.class);

        PageRequest pageable =
                PageRequest.of(0,10);

        Page<Payment> page =
                new PageImpl<>(
                        List.of(payment),
                        pageable,
                        1
                );

        when(paymentRepository.findAll(
                ArgumentMatchers.<Specification<Payment>>any(),
                eq(pageable)
        ))
                .thenReturn(page);

        when(paymentMapper.toResponse(payment))
                .thenReturn(
                        mock(PaymentResponse.class)
                );

        Page<PaymentResponse> result =
                paymentService.findAll(
                        filter,
                        pageable
                );

        assertNotNull(result);

        assertEquals(
                1,
                result.getTotalElements()
        );

        verify(paymentRepository)
                .findAll(
                        ArgumentMatchers.<Specification<Payment>>any(),
                        eq(pageable)
                );

        verify(paymentMapper)
                .toResponse(payment);
    }

    @Test
    void shouldUpdatePaymentSuccessfully(){

        UpdatePaymentRequest request =
                mock(UpdatePaymentRequest.class);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(
                        mock(PaymentResponse.class)
                );

        PaymentResponse response =
                paymentService.update(
                        paymentId,
                        request
                );

        assertNotNull(response);

        verify(paymentMapper)
                .updateEntity(
                        payment,
                        request
                );

        verify(paymentRepository)
                .save(payment);

        verify(paymentEventService)
                .record(
                        eq(payment),
                        any(),
                        anyString()
                );
    }
    @Test
    void shouldUpdatePaymentStatusSuccessfully(){

        payment.setStatus(PaymentStatus.PROCESSING);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(
                        mock(PaymentResponse.class)
                );

        PaymentResponse response =
                paymentService.updateStatus(
                        paymentId,
                        PaymentStatus.APPROVED
                );

        assertNotNull(response);

        verify(paymentRepository)
                .save(payment);

        verify(paymentStatusHistoryService)
                .record(
                        eq(payment),
                        eq(PaymentStatus.PROCESSING),
                        eq(PaymentStatus.APPROVED)
                );

        verify(paymentEventService)
                .record(
                        eq(payment),
                        any(),
                        anyString()
                );
    }

    @Test
    void shouldThrowExceptionWhenPaymentDoesNotExist(){

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        paymentService.delete(paymentId)
        );
    }

    @Test
    void shouldCreatePaymentSuccessfully(){

        CreatePaymentRequest request =
                mock(CreatePaymentRequest.class);

        UUID customerId = UUID.randomUUID();

        Customer customer = new Customer();

        when(request.customerId())
                .thenReturn(customerId);

        when(paymentRepository.findByIdempotencyKey("idem-123"))
                .thenReturn(Optional.empty());

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        Payment payment = new Payment();

        when(paymentMapper.toEntity(request, customer))
                .thenReturn(payment);

        when(paymentReferenceService.generateReference())
                .thenReturn("PAY-20260803-ABC123");

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(
                        mock(PaymentResponse.class)
                );

        PaymentResponse response =
                paymentService.create(
                        "idem-123",
                        request
                );

        assertNotNull(response);

        verify(paymentEventService)
                .record(
                        payment,
                        PaymentEventType.PAYMENT_CREATED,
                        "Payment created successfully"
                );
    }

    @Test
    void shouldReturnExistingPaymentWhenIdempotencyKeyExists(){

        Payment existing = new Payment();

        when(paymentRepository.findByIdempotencyKey("idem-123"))
                .thenReturn(Optional.of(existing));

        when(paymentMapper.toResponse(existing))
                .thenReturn(
                        mock(PaymentResponse.class)
                );

        PaymentResponse response =
                paymentService.create(
                        "idem-123",
                        mock(CreatePaymentRequest.class)
                );

        assertNotNull(response);

        verify(paymentRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound(){

        CreatePaymentRequest request =
                mock(CreatePaymentRequest.class);

        UUID customerId = UUID.randomUUID();

        when(request.customerId())
                .thenReturn(customerId);

        when(paymentRepository.findByIdempotencyKey(any()))
                .thenReturn(Optional.empty());

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () ->
                        paymentService.create(
                                "idem",
                                request
                        )
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingMissingPayment(){

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        paymentService.update(
                                paymentId,
                                mock(UpdatePaymentRequest.class)
                        )
        );
    }

    @Test
    void shouldRejectInvalidPaymentStatusTransition(){

        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        assertThrows(
                InvalidPaymentStatusTransitionException.class,
                () ->
                        paymentService.updateStatus(
                                paymentId,
                                PaymentStatus.APPROVED
                        )
        );
    }

    @Test
    void shouldReturnPaymentWhenStatusAlreadyExists(){

        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentMapper.toResponse(payment))
                .thenReturn(
                        mock(PaymentResponse.class)
                );

        PaymentResponse response =
                paymentService.updateStatus(
                        paymentId,
                        PaymentStatus.PENDING
                );

        assertNotNull(response);

        verify(paymentRepository, never())
                .save(any());
    }

    @Test
    void shouldCancelPaymentSuccessfully(){

        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        paymentService.cancel(paymentId);

        verify(paymentStatusHistoryService)
                .record(
                        eq(payment),
                        eq(PaymentStatus.PENDING),
                        eq(PaymentStatus.CANCELLED)
                );
    }

    @Test
    void shouldRefundPaymentSuccessfully(){

        payment.setStatus(PaymentStatus.APPROVED);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        paymentService.refund(paymentId);

        verify(paymentStatusHistoryService)
                .record(
                        eq(payment),
                        eq(PaymentStatus.APPROVED),
                        eq(PaymentStatus.REFUNDED)
                );
    }

    @Test
    void shouldRetryDeclinedPaymentSuccessfully(){

        payment.setStatus(PaymentStatus.DECLINED);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        when(paymentMapper.toResponse(any()))
                .thenReturn(
                        mock(PaymentResponse.class)
                );

        PaymentResponse response =
                paymentService.retry(paymentId);

        assertNotNull(response);

        verify(paymentEventService)
                .record(
                        any(),
                        eq(PaymentEventType.PAYMENT_RETRY_CREATED),
                        anyString()
                );
    }

    @Test
    void shouldRejectRetryWhenPaymentIsNotDeclined(){

        payment.setStatus(PaymentStatus.APPROVED);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        assertThrows(
                InvalidPaymentRetryException.class,
                () ->
                        paymentService.retry(paymentId)
        );
    }

    @Test
    void shouldExpirePaymentSuccessfully(){

        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        paymentService.expire(paymentId);

        verify(paymentEventService)
                .record(
                        eq(payment),
                        eq(PaymentEventType.PAYMENT_EXPIRED),
                        anyString()
                );
    }

    @Test
    void shouldThrowExceptionWhenPendingPaymentExpired(){

        payment.setStatus(PaymentStatus.PENDING);

        payment.setExpiresAt(
                LocalDateTime.now()
                        .minusMinutes(1)
        );

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        assertThrows(
                PaymentExpiredException.class,
                () ->
                        paymentService.updateStatus(
                                paymentId,
                                PaymentStatus.PROCESSING
                        )
        );
    }

    @Test
    void shouldFindPaymentByReferenceSuccessfully(){

        when(paymentRepository.findByReference("REF-123"))
                .thenReturn(Optional.of(payment));

        when(paymentMapper.toResponse(payment))
                .thenReturn(
                        mock(PaymentResponse.class)
                );

        assertNotNull(
                paymentService.findByReference("REF-123")
        );
    }

    @Test
    void shouldThrowExceptionWhenPaymentReferenceNotFound(){

        when(paymentRepository.findByReference("REF-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        paymentService.findByReference("REF-999")
        );

        verify(paymentRepository)
                .findByReference("REF-999");
    }

    @Test
    void shouldExpirePaymentAutomaticallyWhenExpirationDatePassed(){

        payment.setStatus(PaymentStatus.PENDING);

        payment.setExpiresAt(
                LocalDateTime.now()
                        .minusMinutes(10)
        );

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        assertThrows(
                PaymentExpiredException.class,
                () ->
                        paymentService.updateStatus(
                                paymentId,
                                PaymentStatus.PROCESSING
                        )
        );

        verify(paymentRepository)
                .save(payment);

        verify(paymentEventService)
                .record(
                        eq(payment),
                        eq(PaymentEventType.PAYMENT_EXPIRED),
                        eq("Payment expired automatically")
                );
    }

    @Test
    void shouldReturnSamePaymentWhenStatusAlreadyUpdated(){

        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentMapper.toResponse(payment))
                .thenReturn(mock(PaymentResponse.class));

        PaymentResponse response =
                paymentService.updateStatus(
                        paymentId,
                        PaymentStatus.PENDING
                );

        assertNotNull(response);

        verify(paymentRepository, never())
                .save(any());

        verify(paymentEventService, never())
                .record(
                        any(),
                        any(),
                        anyString()
                );
    }

    @Test
    void shouldNotExpirePendingPaymentWithoutExpirationDate(){

        payment.setStatus(PaymentStatus.PENDING);
        payment.setExpiresAt(null);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(mock(PaymentResponse.class));

        paymentService.updateStatus(
                paymentId,
                PaymentStatus.PROCESSING
        );

        verify(paymentEventService, never())
                .record(
                        eq(payment),
                        eq(PaymentEventType.PAYMENT_EXPIRED),
                        anyString()
                );
    }

    @Test
    void shouldNotValidateExpirationForApprovedPayment(){

        payment.setStatus(PaymentStatus.APPROVED);

        payment.setExpiresAt(
                LocalDateTime.now()
                        .minusMinutes(10)
        );

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(mock(PaymentResponse.class));

        paymentService.updateStatus(
                paymentId,
                PaymentStatus.REFUNDED
        );

        verify(paymentRepository)
                .save(payment);
    }

    @Test
    void shouldThrowPaymentExpiredExceptionWhenPaymentIsExpired() {

        UUID paymentId = UUID.randomUUID();

        Payment payment = Payment.builder()
                .id(paymentId)
                .status(PaymentStatus.PENDING)
                .expiresAt(
                        LocalDateTime.now()
                                .minusMinutes(10)
                )
                .build();

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        assertThrows(
                PaymentExpiredException.class,
                () -> paymentService.updateStatus(
                        paymentId,
                        PaymentStatus.APPROVED
                )
        );

        verify(paymentRepository)
                .save(payment);

        verify(paymentStatusHistoryService)
                .record(
                        eq(payment),
                        eq(PaymentStatus.PENDING),
                        eq(PaymentStatus.EXPIRED)
                );

        verify(paymentEventService)
                .record(
                        eq(payment),
                        eq(PaymentEventType.PAYMENT_EXPIRED),
                        eq("Payment expired automatically")
                );
    }

    @Test
    void shouldNotExpirePaymentWhenStatusIsNotPending() {

        UUID paymentId = UUID.randomUUID();

        Payment payment = Payment.builder()
                .id(paymentId)
                .status(PaymentStatus.APPROVED)
                .expiresAt(LocalDateTime.now().minusMinutes(10))
                .build();

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(mock(PaymentResponse.class));

        paymentService.updateStatus(
                paymentId,
                PaymentStatus.REFUNDED
        );

        verify(paymentRepository)
                .save(payment);

        verify(paymentStatusHistoryService)
                .record(
                        eq(payment),
                        eq(PaymentStatus.APPROVED),
                        eq(PaymentStatus.REFUNDED)
                );
        verify(paymentEventService)
                .record(
                        eq(payment),
                        eq(PaymentEventType.PAYMENT_STATUS_CHANGED),
                        any()
                );
    }

    @Test
    void shouldNotExpirePaymentWhenExpirationDateIsNull() {

        UUID paymentId = UUID.randomUUID();

        Payment payment = Payment.builder()
                .id(paymentId)
                .status(PaymentStatus.PENDING)
                .expiresAt(null)
                .build();

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(mock(PaymentResponse.class));

        paymentService.updateStatus(
                paymentId,
                PaymentStatus.PROCESSING
        );

        verify(paymentRepository)
                .save(payment);

        verify(paymentEventService)
                .record(
                        eq(payment),
                        eq(PaymentEventType.PAYMENT_STATUS_CHANGED),
                        eq("Status changed from PENDING to PROCESSING")
                );

        verify(paymentEventService, never())
                .record(
                        eq(payment),
                        eq(PaymentEventType.PAYMENT_EXPIRED),
                        any()
                );
    }

    @Test
    void shouldNotExpirePaymentWhenExpirationDateIsInFuture() {

        UUID paymentId = UUID.randomUUID();

        Payment payment = Payment.builder()
                .id(paymentId)
                .status(PaymentStatus.PENDING)
                .expiresAt(
                        LocalDateTime.now()
                                .plusMinutes(30)
                )
                .build();

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(mock(PaymentResponse.class));

        paymentService.updateStatus(
                paymentId,
                PaymentStatus.PROCESSING
        );

        verify(paymentRepository)
                .save(payment);

        verify(paymentStatusHistoryService)
                .record(
                        eq(payment),
                        eq(PaymentStatus.PENDING),
                        eq(PaymentStatus.PROCESSING)
                );

        verify(paymentEventService)
                .record(
                        eq(payment),
                        eq(PaymentEventType.PAYMENT_STATUS_CHANGED),
                        any()
                );

        verify(paymentEventService, never())
                .record(
                        eq(payment),
                        eq(PaymentEventType.PAYMENT_EXPIRED),
                        any()
                );
    }
}