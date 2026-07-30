package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentEventType;
import com.well.tech.next.pay.dto.response.payment.PaymentEventResponse;
import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.entity.PaymentEvent;
import com.well.tech.next.pay.repository.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventService {

    private final PaymentEventRepository repository;

    public void record(
            Payment payment,
            PaymentEventType eventType,
            String description
    ) {

        log.info(
                "Recording payment event. paymentId={}, eventType={}",
                payment.getId(),
                eventType
        );

        PaymentEvent event = PaymentEvent.builder()
                .payment(payment)
                .eventType(eventType)
                .description(description)
                .build();

        repository.save(event);

        log.info(
                "Payment event recorded successfully. paymentId={}, eventType={}",
                payment.getId(),
                eventType
        );
    }

    @Transactional(readOnly = true)
    public List<PaymentEventResponse> findByPaymentId(UUID paymentId) {

        log.info("Finding payment events. paymentId={}", paymentId);

        return repository.findByPaymentIdOrderByCreatedAtDesc(paymentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentEventResponse toResponse(PaymentEvent event) {
        return new PaymentEventResponse(
                event.getId(),
                event.getEventType(),
                event.getDescription(),
                event.getCreatedAt()
        );
    }
}