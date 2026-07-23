package com.well.tech.next.pay.service;

import com.well.tech.next.pay.dto.request.payment.PaymentWebhookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentWebhookService {

    private final PaymentService paymentService;

    public void process(PaymentWebhookRequest request) {
        paymentService.updateStatus(
                request.paymentId(),
                request.status()
        );
    }
}