package com.well.tech.next.pay.service;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookService {

    private final PaymentRepository paymentRepository;
    private final WebhookEventRepository webhookEventRepository;

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

                    return new PaymentNotFoundException(
                            request.paymentId()
                    );
                });

        payment.setStatus(request.status());

        paymentRepository.save(payment);

        WebhookEvent webhookEvent = WebhookEvent.builder()
                .eventId(request.eventId())
                .paymentId(request.paymentId())
                .status(request.status())
                .build();

        webhookEventRepository.save(webhookEvent);

        log.info(
                "Payment webhook processed successfully. eventId={}, paymentId={}, status={}",
                request.eventId(),
                request.paymentId(),
                request.status()
        );
    }
}