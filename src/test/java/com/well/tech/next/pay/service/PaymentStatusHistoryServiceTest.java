package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.validation.PaymentNotFoundException;
import com.well.tech.next.pay.dto.response.payment.PaymentStatusHistoryResponse;
import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.entity.PaymentStatusHistory;
import com.well.tech.next.pay.mapper.PaymentStatusHistoryMapper;
import com.well.tech.next.pay.repository.PaymentRepository;
import com.well.tech.next.pay.repository.PaymentStatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentStatusHistoryServiceTest {

    private PaymentStatusHistoryRepository historyRepository;
    private PaymentRepository paymentRepository;
    private PaymentStatusHistoryMapper mapper;

    private PaymentStatusHistoryService service;

    @BeforeEach
    void setup() {

        historyRepository =
                mock(PaymentStatusHistoryRepository.class);

        paymentRepository =
                mock(PaymentRepository.class);

        mapper =
                mock(PaymentStatusHistoryMapper.class);

        service =
                new PaymentStatusHistoryService(
                        historyRepository,
                        paymentRepository,
                        mapper
                );
    }

    @Test
    void shouldRecordPaymentStatusHistorySuccessfully() {

        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .build();

        service.record(
                payment,
                PaymentStatus.PENDING,
                PaymentStatus.PROCESSING
        );

        verify(historyRepository)
                .save(any(PaymentStatusHistory.class));
    }

    @Test
    void shouldFindPaymentStatusHistorySuccessfully() {

        UUID paymentId = UUID.randomUUID();

        Payment payment = Payment.builder()
                .id(paymentId)
                .build();

        PaymentStatusHistory history =
                PaymentStatusHistory.builder()
                        .id(UUID.randomUUID())
                        .payment(payment)
                        .fromStatus(PaymentStatus.PENDING)
                        .toStatus(PaymentStatus.PROCESSING)
                        .build();

        PaymentStatusHistoryResponse response =
                mock(PaymentStatusHistoryResponse.class);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(historyRepository
                .findByPaymentIdOrderByCreatedAtAsc(paymentId))
                .thenReturn(List.of(history));

        when(mapper.toResponse(history))
                .thenReturn(response);

        List<PaymentStatusHistoryResponse> result =
                service.findByPaymentId(paymentId);

        assertEquals(1, result.size());

        verify(paymentRepository)
                .findById(paymentId);

        verify(historyRepository)
                .findByPaymentIdOrderByCreatedAtAsc(paymentId);

        verify(mapper)
                .toResponse(history);
    }

    @Test
    void shouldThrowExceptionWhenPaymentDoesNotExist() {

        UUID paymentId = UUID.randomUUID();

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        assertThrows(
                PaymentNotFoundException.class,
                () ->
                        service.findByPaymentId(paymentId)
        );

        verify(historyRepository, never())
                .findByPaymentIdOrderByCreatedAtAsc(any());
    }
}