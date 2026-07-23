package com.well.tech.next.pay.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.well.tech.next.pay.common.exceptions.validation.InvalidWebhookSignatureException;
import com.well.tech.next.pay.config.ApiVersion;
import com.well.tech.next.pay.dto.request.payment.PaymentWebhookRequest;
import com.well.tech.next.pay.service.PaymentWebhookService;
import com.well.tech.next.pay.service.WebhookSignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final WebhookSignatureService webhookSignatureService;
    private final ObjectMapper objectMapper;

    @PostMapping("/payments")
    public ResponseEntity<Void> handlePaymentWebhook(
            @RequestHeader("X-Webhook-Timestamp")
            String timestamp,

            @RequestHeader("X-Webhook-Signature")
            String signature,

            @RequestBody
            String rawPayload
    ) throws JsonProcessingException {

        if (!webhookSignatureService.isTimestampValid(timestamp)) {
            throw new InvalidWebhookSignatureException("Invalid webhook signature");
        }

        if (!webhookSignatureService.isValid(
                rawPayload,
                timestamp,
                signature
        )) {
            throw new InvalidWebhookSignatureException("Invalid webhook signature");
        }

        PaymentWebhookRequest request =
                objectMapper.readValue(
                        rawPayload,
                        PaymentWebhookRequest.class
                );

        paymentWebhookService.process(request);

        return ResponseEntity.ok().build();
    }
}