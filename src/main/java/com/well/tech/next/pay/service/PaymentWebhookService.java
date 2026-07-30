package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentEventType;
import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.validation.PaymentExpiredException;
import com.well.tech.next.pay.common.exceptions.validation.PaymentNotFoundException;
import com.well.tech.next.pay.dto.request.payment.PaymentWebhookRequest;
import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.entity.WebhookEvent;
import com.well.tech.next.pay.repository.PaymentRepository;
import com.well.tech.next.pay.repository.WebhookEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookService {

    private final PaymentRepository paymentRepository;
    private final WebhookEventRepository webhookEventRepository;
    private final PaymentStatusTransitionService paymentStatusTransitionService;
    private final PaymentStatusHistoryService paymentStatusHistoryService;
    private final PaymentEventService paymentEventService;

    @Transactional
    public void process(PaymentWebhookRequest request) {

        log.info(
                "Processing payment webhook. eventId={}, paymentId={}, status={}",
                request.eventId(),
                request.paymentId(),
                request.status()
        );

        if (webhookEventRepository.existsByEventId(request.eventId())) {

            log.info(
                    "Duplicate payment webhook ignored. eventId={}",
                    request.eventId()
            );

            return;
        }

        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> {
                    log.warn(
                            "Payment not found for webhook. eventId={}, paymentId={}",
                            request.eventId(),
                            request.paymentId()
                    );

                    return new PaymentNotFoundException(request.paymentId());
                });

        validateExpiration(payment);

        PaymentStatus currentStatus = payment.getStatus();
        PaymentStatus newStatus = request.status();

        paymentStatusTransitionService.validate(
                currentStatus,
                newStatus
        );

        payment.setStatus(newStatus);

        Payment updatedPayment = paymentRepository.save(payment);

        paymentStatusHistoryService.record(
                updatedPayment,
                currentStatus,
                newStatus
        );

        paymentEventService.record(
                updatedPayment,
                PaymentEventType.WEBHOOK_PROCESSED,
                String.format(
                        "Webhook processed. Status changed from %s to %s",
                        currentStatus,
                        newStatus
                )
        );

        webhookEventRepository.save(
                WebhookEvent.builder()
                        .eventId(request.eventId())
                        .paymentId(request.paymentId())
                        .status(newStatus)
                        .build()
        );

        log.info(
                "Payment webhook processed successfully. eventId={}, paymentId={}, fromStatus={}, toStatus={}",
                request.eventId(),
                request.paymentId(),
                currentStatus,
                newStatus
        );
    }

    private void validateExpiration(Payment payment) {

        if (payment.getStatus() == PaymentStatus.PENDING
                && payment.getExpiresAt() != null
                && payment.getExpiresAt().isBefore(LocalDateTime.now())) {

            log.warn(
                    "Expired payment cannot be processed by webhook. paymentId={}, expiresAt={}",
                    payment.getId(),
                    payment.getExpiresAt()
            );

            throw new PaymentExpiredException(
                    "Payment has expired"
            );
        }
    }
}