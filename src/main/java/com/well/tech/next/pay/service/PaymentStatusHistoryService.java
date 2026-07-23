package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.validation.PaymentNotFoundException;
import com.well.tech.next.pay.dto.response.payment.PaymentStatusHistoryResponse;
import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.entity.PaymentStatusHistory;
import com.well.tech.next.pay.mapper.PaymentStatusHistoryMapper;
import com.well.tech.next.pay.repository.PaymentRepository;
import com.well.tech.next.pay.repository.PaymentStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentStatusHistoryService {

    private final PaymentStatusHistoryRepository historyRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentStatusHistoryMapper paymentStatusHistoryMapper;

    public void record(
            Payment payment,
            PaymentStatus fromStatus,
            PaymentStatus toStatus
    ) {

        log.info(
                "Recording payment status transition. paymentId={}, fromStatus={}, toStatus={}",
                payment.getId(),
                fromStatus,
                toStatus
        );

        PaymentStatusHistory history =
                PaymentStatusHistory.builder()
                        .payment(payment)
                        .fromStatus(fromStatus)
                        .toStatus(toStatus)
                        .createdAt(LocalDateTime.now())
                        .build();

        historyRepository.save(history);

        log.info(
                "Payment status transition recorded successfully. paymentId={}, fromStatus={}, toStatus={}",
                payment.getId(),
                fromStatus,
                toStatus
        );
    }

    @Transactional(readOnly = true)
    public List<PaymentStatusHistoryResponse> findByPaymentId(
            UUID paymentId
    ) {

        log.debug(
                "Fetching payment status history. paymentId={}",
                paymentId
        );

        paymentRepository.findById(paymentId)
                .orElseThrow(() -> {

                    log.warn(
                            "Cannot fetch payment status history. Payment not found. paymentId={}",
                            paymentId
                    );

                    return new PaymentNotFoundException(paymentId);
                });

        List<PaymentStatusHistoryResponse> history =
                historyRepository
                        .findByPaymentIdOrderByCreatedAtAsc(paymentId)
                        .stream()
                        .map(paymentStatusHistoryMapper::toResponse)
                        .toList();

        log.debug(
                "Payment status history fetched successfully. paymentId={}, entries={}",
                paymentId,
                history.size()
        );

        return history;
    }
}