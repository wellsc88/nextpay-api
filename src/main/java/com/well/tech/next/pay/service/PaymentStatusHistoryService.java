package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.validation.PaymentNotFoundException;
import com.well.tech.next.pay.dto.response.payment.PaymentStatusHistoryResponse;
import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.entity.PaymentStatusHistory;
import com.well.tech.next.pay.mapper.PaymentStatusHistoryMapper;
import com.well.tech.next.pay.repository.PaymentRepository;
import com.well.tech.next.pay.repository.PaymentStatusHistoryRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentStatusHistoryService {

    private final PaymentStatusHistoryRepository historyRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentStatusHistoryMapper paymentStatusHistoryMapper;

    public void record(
            Payment payment,
            PaymentStatus fromStatus,
            PaymentStatus toStatus
    ) {
        PaymentStatusHistory history =
                PaymentStatusHistory.builder()
                        .payment(payment)
                        .fromStatus(fromStatus)
                        .toStatus(toStatus)
                        .createdAt(LocalDateTime.now())
                        .build();

        historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<PaymentStatusHistoryResponse> findByPaymentId(
            UUID paymentId
    ) {
        paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(paymentId)
                );

        return historyRepository
                .findByPaymentIdOrderByCreatedAtAsc(paymentId)
                .stream()
                .map(paymentStatusHistoryMapper::toResponse)
                .toList();
    }}