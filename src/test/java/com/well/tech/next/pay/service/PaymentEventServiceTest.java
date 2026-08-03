package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentEventType;
import com.well.tech.next.pay.dto.response.payment.PaymentEventResponse;
import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.entity.PaymentEvent;
import com.well.tech.next.pay.repository.PaymentEventRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentEventServiceTest {

    private PaymentEventRepository repository;

    private PaymentEventService service;

    @BeforeEach
    void setup() {

        repository =
                mock(PaymentEventRepository.class);

        service =
                new PaymentEventService(
                        repository
                );
    }

    private Payment payment() {

        return Payment.builder()
                .id(UUID.randomUUID())
                .build();
    }

    @Test
    void shouldRecordPaymentEventSuccessfully() {

        Payment payment =
                payment();

        service.record(
                payment,
                PaymentEventType.PAYMENT_CREATED,
                "Payment created"
        );

        verify(repository)
                .save(any(PaymentEvent.class));
    }

    @Test
    void shouldFindPaymentEventsSuccessfully() {

        UUID paymentId =  UUID.randomUUID();

        PaymentEvent event =
                PaymentEvent.builder()
                        .id(UUID.randomUUID())
                        .eventType(
                                PaymentEventType.PAYMENT_CREATED
                        )
                        .description(
                                "Payment approved"
                        )
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        when(repository
                .findByPaymentIdOrderByCreatedAtDesc(paymentId))
                .thenReturn(
                        List.of(event)
                );

        List<PaymentEventResponse> response =
                service.findByPaymentId(paymentId);

        assertEquals(
                1,
                response.size()
        );

        assertEquals(
                PaymentEventType.PAYMENT_CREATED,
                response.getFirst().eventType()
        );

        assertEquals(
                "Payment approved",
                response.getFirst().description()
        );
    }

    @Test
    void shouldReturnEmptyListWhenNoEventsExist() {

        UUID paymentId = UUID.randomUUID();

        when(repository
                .findByPaymentIdOrderByCreatedAtDesc(paymentId))
                .thenReturn(
                        List.of()
                );

        List<PaymentEventResponse> response =
                service.findByPaymentId(paymentId);

        assertTrue(
                response.isEmpty()
        );
    }
}