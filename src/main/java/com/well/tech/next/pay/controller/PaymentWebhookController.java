package com.well.tech.next.pay.controller;

import com.well.tech.next.pay.config.ApiVersion;
import com.well.tech.next.pay.dto.request.payment.PaymentWebhookRequest;
import com.well.tech.next.pay.service.PaymentWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        ApiVersion.API_BASE_PATH
                + "/"
                + ApiVersion.API_VERSION
                + "/webhooks"
)
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentWebhookService paymentWebhookService;

    @PostMapping("/payments")
    public ResponseEntity<Void> handlePaymentWebhook(
            @RequestBody PaymentWebhookRequest request
    ) {
        paymentWebhookService.process(request);

        return ResponseEntity.ok().build();
    }
}