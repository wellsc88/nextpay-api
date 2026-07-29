package com.well.tech.next.pay.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.well.tech.next.pay.common.exceptions.validation.InvalidWebhookSignatureException;
import com.well.tech.next.pay.config.ApiVersion;
import com.well.tech.next.pay.dto.request.payment.PaymentWebhookRequest;
import com.well.tech.next.pay.service.PaymentWebhookService;
import com.well.tech.next.pay.service.WebhookSignatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Payment Webhooks",
        description = "Endpoints for receiving external payment provider webhooks"
)
public class PaymentWebhookController {

    private final PaymentWebhookService paymentWebhookService;
    private final WebhookSignatureService webhookSignatureService;
    private final ObjectMapper objectMapper;


    @Operation(
            summary = "Process payment webhook",
            description = """
                    Receives payment events from external providers.
                    The request must contain a valid timestamp and HMAC signature
                    to ensure webhook authenticity.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Webhook processed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid webhook payload",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid webhook signature",
                    content = @Content
            )
    })
    @PostMapping("/payments")
    public ResponseEntity<Void> handlePaymentWebhook(

            @Parameter(
                    description = "Webhook request timestamp used for signature validation",
                    required = true,
                    example = "1723456789",
                    in = ParameterIn.HEADER
            )
            @RequestHeader("X-Webhook-Timestamp")
            String timestamp,


            @Parameter(
                    description = "HMAC SHA-256 webhook signature",
                    required = true,
                    example = "a8f7d8c91e4b6f8d7c2e1f...",
                    in = ParameterIn.HEADER
            )
            @RequestHeader("X-Webhook-Signature")
            String signature,


            @Parameter(
                    description = "Raw webhook payload received from provider",
                    required = true
            )
            @RequestBody
            String rawPayload

    ) throws JsonProcessingException {

        if (!webhookSignatureService.isTimestampValid(timestamp)) {
            throw new InvalidWebhookSignatureException(
                    "Invalid webhook signature"
            );
        }

        if (!webhookSignatureService.isValid(
                rawPayload,
                timestamp,
                signature
        )) {
            throw new InvalidWebhookSignatureException(
                    "Invalid webhook signature"
            );
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