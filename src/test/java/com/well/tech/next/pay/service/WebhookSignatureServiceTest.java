package com.well.tech.next.pay.service;

import com.well.tech.next.pay.config.WebhookProperties;
import com.well.tech.next.pay.service.abstraction.MacFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.crypto.Mac;
import java.security.GeneralSecurityException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebhookSignatureServiceTest {

    private WebhookSignatureService webhookSignatureService;

    private MacFactory macFactory;

    @BeforeEach
    void setup() {

        WebhookProperties webhookProperties = Mockito.mock(WebhookProperties.class);

        macFactory = Mockito.mock(MacFactory.class);

        when(webhookProperties.decodedSecret())
                .thenReturn(
                        "nextpay-webhook-secret".getBytes()
                );

        webhookSignatureService =
                new WebhookSignatureService(
                        webhookProperties,
                        macFactory
                );
    }

    @Test
    void shouldValidateTimestampSuccessfully() {

        String timestamp =
                String.valueOf(
                        Instant.now().getEpochSecond()
                );

        assertTrue(
                webhookSignatureService
                        .isTimestampValid(timestamp)
        );
    }

    @Test
    void shouldRejectExpiredTimestamp() {

        String timestamp =
                String.valueOf(
                        Instant.now()
                                .minusSeconds(600)
                                .getEpochSecond()
                );
        assertFalse(
                webhookSignatureService
                        .isTimestampValid(timestamp)
        );
    }

    @Test
    void shouldRejectFutureTimestamp() {

        String timestamp =
                String.valueOf(
                        Instant.now()
                                .plusSeconds(600)
                                .getEpochSecond()
                );

        assertFalse(
                webhookSignatureService
                        .isTimestampValid(timestamp)
        );
    }

    @Test
    void shouldRejectInvalidTimestampFormat() {

        assertFalse(
                webhookSignatureService
                        .isTimestampValid("invalid")
        );
    }

    @Test
    void shouldGenerateSignatureSuccessfully()
            throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");

        when(macFactory.create("HmacSHA256"))
                .thenReturn(mac);

        String signature =
                webhookSignatureService
                        .generateSignature(
                                "payment-test"
                        );

        assertNotNull(signature);
        assertEquals(
                64,
                signature.length()
        );
    }

    @Test
    void shouldGenerateDifferentSignatureForDifferentPayloads()
            throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");

        when(macFactory.create("HmacSHA256"))
                .thenReturn(mac);

        String first =
                webhookSignatureService
                        .generateSignature(
                                "payload-1"
                        );

        String second =
                webhookSignatureService
                        .generateSignature(
                                "payload-2"
                        );

        assertNotEquals(
                first,
                second
        );
    }

    @Test
    void shouldValidateWebhookSignatureSuccessfully()
            throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");

        when(macFactory.create("HmacSHA256"))
                .thenReturn(mac);

        String timestamp =
                "1723456789";

        String payload =
                "{\"status\":\"APPROVED\"}";

        String signature =
                webhookSignatureService
                        .generateSignature(
                                timestamp + "." + payload
                        );

        assertTrue(
                webhookSignatureService
                        .isValid(
                                payload,
                                timestamp,
                                signature
                        )
        );
    }

    @Test
    void shouldRejectInvalidWebhookSignature()
            throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");

        when(macFactory.create("HmacSHA256"))
                .thenReturn(mac);

        assertFalse(
                webhookSignatureService
                        .isValid(
                                "{\"status\":\"APPROVED\"}",
                                "1723456789",
                                "invalid-signature"
                        )
        );
    }

    @Test
    void shouldRejectEmptyWebhookSignature()
            throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");

        when(macFactory.create("HmacSHA256"))
                .thenReturn(mac);

        assertFalse(
                webhookSignatureService
                        .isValid(
                                "{\"status\":\"APPROVED\"}",
                                "1723456789",
                                ""
                        )
        );
    }

    @Test
    void shouldGenerateSignatureWithEmptyPayload()
            throws Exception {

        Mac mac =
                Mac.getInstance("HmacSHA256");


        when(macFactory.create("HmacSHA256"))
                .thenReturn(mac);


        String signature =
                webhookSignatureService
                        .generateSignature("");

        assertNotNull(signature);
        assertEquals(
                64,
                signature.length()
        );
    }

    @Test
    void shouldThrowExceptionWhenMacGenerationFails()
            throws Exception {

        when(macFactory.create("HmacSHA256"))
                .thenThrow(
                        new GeneralSecurityException(
                                "test error"
                        )
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                webhookSignatureService
                                        .generateSignature(
                                                "payload"
                                        )
                );

        assertEquals(
                "Failed to generate webhook signature",
                exception.getMessage()
        );
    }
}