package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.validation.PaymentNotFoundException;
import com.well.tech.next.pay.dto.response.payment.PaymentTimelineResponse;
import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.entity.PaymentEvent;
import com.well.tech.next.pay.entity.PaymentStatusHistory;
import com.well.tech.next.pay.repository.PaymentEventRepository;
import com.well.tech.next.pay.repository.PaymentRepository;
import com.well.tech.next.pay.repository.PaymentStatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentTimelineServiceTest {

    private PaymentRepository paymentRepository;
    private PaymentEventRepository paymentEventRepository;
    private PaymentStatusHistoryRepository paymentStatusHistoryRepository;

    private PaymentTimelineService service;

    @BeforeEach
    void setup() {

        paymentRepository = mock(PaymentRepository.class);
        paymentEventRepository = mock(PaymentEventRepository.class);
        paymentStatusHistoryRepository = mock(PaymentStatusHistoryRepository.class);

        service = new PaymentTimelineService(
                paymentEventRepository,
                paymentStatusHistoryRepository,
                paymentRepository
        );
    }

    @Test
    void shouldFindTimelineSuccessfully() {

        UUID paymentId = UUID.randomUUID();

        Payment payment = Payment.builder()
                .id(paymentId)
                .build();

        LocalDateTime eventDate =
                LocalDateTime.now().minusMinutes(10);

        LocalDateTime statusDate =
                LocalDateTime.now();

        PaymentEvent event =
                PaymentEvent.builder()
                        .id(UUID.randomUUID())
                        .description("Payment created")
                        .createdAt(eventDate)
                        .build();

        PaymentStatusHistory history =
                PaymentStatusHistory.builder()
                        .id(UUID.randomUUID())
                        .fromStatus(PaymentStatus.PENDING)
                        .toStatus(PaymentStatus.PROCESSING)
                        .createdAt(statusDate)
                        .build();

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(paymentEventRepository
                .findByPaymentIdOrderByCreatedAtAsc(paymentId))
                .thenReturn(List.of(event));

        when(paymentStatusHistoryRepository
                .findByPaymentIdOrderByCreatedAtAsc(paymentId))
                .thenReturn(List.of(history));

        List<PaymentTimelineResponse> result =
                service.findTimeline(paymentId);

        assertEquals(2, result.size());

        assertEquals(
                "EVENT",
                result.get(0).type()
        );

        assertEquals(
                "STATUS",
                result.get(1).type()
        );

        verify(paymentEventRepository)
                .findByPaymentIdOrderByCreatedAtAsc(paymentId);

        verify(paymentStatusHistoryRepository)
                .findByPaymentIdOrderByCreatedAtAsc(paymentId);
    }

    @Test
    void shouldReturnEmptyTimelineWhenNoEventsOrHistory() {

        UUID paymentId = UUID.randomUUID();

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(new Payment()));

        when(paymentEventRepository
                .findByPaymentIdOrderByCreatedAtAsc(paymentId))
                .thenReturn(List.of());

        when(paymentStatusHistoryRepository
                .findByPaymentIdOrderByCreatedAtAsc(paymentId))
                .thenReturn(List.of());

        List<PaymentTimelineResponse> result =
                service.findTimeline(paymentId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFound() {

        UUID paymentId = UUID.randomUUID();

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () -> service.findTimeline(paymentId)
        );

        verify(paymentEventRepository, never())
                .findByPaymentIdOrderByCreatedAtAsc(any());

        verify(paymentStatusHistoryRepository, never())
                .findByPaymentIdOrderByCreatedAtAsc(any());
    }
}