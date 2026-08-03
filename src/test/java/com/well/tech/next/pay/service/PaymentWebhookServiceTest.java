package com.well.tech.next.pay.service;

import static org.junit.jupiter.api.Assertions.*;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.validation.PaymentExpiredException;
import com.well.tech.next.pay.common.exceptions.validation.PaymentNotFoundException;
import com.well.tech.next.pay.dto.request.payment.PaymentWebhookRequest;
import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.entity.WebhookEvent;
import com.well.tech.next.pay.repository.PaymentRepository;
import com.well.tech.next.pay.repository.WebhookEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentWebhookServiceTest {

    private PaymentWebhookService paymentWebhookService;

    private PaymentRepository paymentRepository;
    private WebhookEventRepository webhookEventRepository;
    private PaymentStatusHistoryService paymentStatusHistoryService;
    private PaymentEventService paymentEventService;

    @BeforeEach
    void setup() {

        paymentRepository =
                mock(PaymentRepository.class);

        webhookEventRepository =
                mock(WebhookEventRepository.class);

        PaymentStatusTransitionService paymentStatusTransitionService =
                mock(PaymentStatusTransitionService.class);

        paymentStatusHistoryService =
                mock(PaymentStatusHistoryService.class);

        paymentEventService =
                mock(PaymentEventService.class);

        paymentWebhookService =
                new PaymentWebhookService(
                        paymentRepository,
                        webhookEventRepository,
                        paymentStatusTransitionService,
                        paymentStatusHistoryService,
                        paymentEventService
                );
    }

    @Test
    void shouldProcessWebhookSuccessfully() {

        UUID paymentId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        Payment payment =
                Payment.builder()
                        .id(paymentId)
                        .status(PaymentStatus.PROCESSING)
                        .build();

        PaymentWebhookRequest request =
                new PaymentWebhookRequest(
                        paymentId,
                        PaymentStatus.APPROVED,
                        eventId.toString()
                );

        when(webhookEventRepository.existsByEventId(eventId.toString()))
                .thenReturn(false);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        paymentWebhookService.process(request);

        verify(paymentRepository)
                .save(payment);

        verify(paymentStatusHistoryService)
                .record(
                        payment,
                        PaymentStatus.PROCESSING,
                        PaymentStatus.APPROVED
                );

        verify(paymentEventService)
                .record(
                        eq(payment),
                        any(),
                        any()
                );

        verify(webhookEventRepository)
                .save(any(WebhookEvent.class));
    }

    @Test
    void shouldIgnoreDuplicateWebhook() {

        UUID eventId = UUID.randomUUID();

        PaymentWebhookRequest request =
                new PaymentWebhookRequest(
                        UUID.randomUUID(),
                        PaymentStatus.APPROVED,
                        eventId.toString()
                );

        when(webhookEventRepository.existsByEventId(eventId.toString()))
                .thenReturn(true);

        paymentWebhookService.process(request);

        verify(paymentRepository, never())
                .findById(any());

        verify(webhookEventRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowWhenPaymentNotFound() {

        UUID paymentId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        PaymentWebhookRequest request =
                new PaymentWebhookRequest(
                        paymentId,
                        PaymentStatus.APPROVED,
                        eventId.toString()
                );

        when(webhookEventRepository.existsByEventId(eventId.toString()))
                .thenReturn(false);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () ->
                        paymentWebhookService.process(request)
        );

        verify(paymentRepository, never())
                .save(any());
    }

    @Test
    void shouldRejectExpiredPaymentWebhook() {

        UUID paymentId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        Payment payment =
                Payment.builder()
                        .id(paymentId)
                        .status(PaymentStatus.PENDING)
                        .expiresAt(
                                LocalDateTime.now()
                                        .minusMinutes(10)
                        )
                        .build();

        PaymentWebhookRequest request =
                new PaymentWebhookRequest(
                        paymentId,
                        PaymentStatus.APPROVED,
                        eventId.toString()
                );

        when(webhookEventRepository.existsByEventId(eventId.toString()))
                .thenReturn(false);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        assertThrows(
                PaymentExpiredException.class,
                () ->
                        paymentWebhookService.process(request)
        );

        verify(paymentRepository, never())
                .save(any());
    }

    @Test
    void shouldAllowPendingPaymentWithoutExpirationDate() {

        UUID paymentId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        Payment payment =
                Payment.builder()
                        .id(paymentId)
                        .status(PaymentStatus.PENDING)
                        .expiresAt(null)
                        .build();

        PaymentWebhookRequest request =
                new PaymentWebhookRequest(
                        paymentId,
                        PaymentStatus.PROCESSING,
                        eventId.toString()
                );

        when(webhookEventRepository.existsByEventId(eventId.toString()))
                .thenReturn(false);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        paymentWebhookService.process(request);

        verify(paymentRepository)
                .save(payment);
    }
    @Test
    void shouldNotExpirePendingPaymentWithFutureExpirationDate() {

        UUID paymentId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        Payment payment =
                Payment.builder()
                        .id(paymentId)
                        .status(PaymentStatus.PENDING)
                        .expiresAt(
                                LocalDateTime.now()
                                        .plusMinutes(30)
                        )
                        .build();

        PaymentWebhookRequest request =
                new PaymentWebhookRequest(
                        paymentId,
                        PaymentStatus.PROCESSING,
                        eventId.toString()
                );

        when(webhookEventRepository.existsByEventId(eventId.toString()))
                .thenReturn(false);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        paymentWebhookService.process(request);

        verify(paymentRepository)
                .save(payment);

        verify(paymentStatusHistoryService)
                .record(
                        payment,
                        PaymentStatus.PENDING,
                        PaymentStatus.PROCESSING
                );

        verify(paymentEventService)
                .record(
                        eq(payment),
                        any(),
                        any()
                );
    }

}