package com.well.tech.next.pay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.well.tech.next.pay.common.exceptions.GlobalExceptionHandler;
import com.well.tech.next.pay.config.ApiVersion;
import com.well.tech.next.pay.security.JwtAuthenticationFilter;
import com.well.tech.next.pay.security.JwtService;
import com.well.tech.next.pay.service.PaymentWebhookService;
import com.well.tech.next.pay.service.WebhookSignatureService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentWebhookController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PaymentWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentWebhookService paymentWebhookService;

    @MockitoBean
    private WebhookSignatureService webhookSignatureService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String basePath() {

        return ApiVersion.API_BASE_PATH
                + "/"
                + ApiVersion.API_VERSION
                + "/webhooks/payments";
    }

    @Test
    void shouldProcessPaymentWebhookSuccessfully()
            throws Exception {

        String paymentId = UUID.randomUUID().toString();

        String payload = """
            {
                "paymentId": "%s",
                "status": "APPROVED"
            }
            """.formatted(paymentId);


        when(webhookSignatureService.isTimestampValid("1723456789"))
                .thenReturn(true);

        when(webhookSignatureService.isValid(
                payload,
                "1723456789",
                "valid-signature"
        ))
                .thenReturn(true);


        mockMvc.perform(
                        post(basePath())
                                .header(
                                        "X-Webhook-Timestamp",
                                        "1723456789"
                                )
                                .header(
                                        "X-Webhook-Signature",
                                        "valid-signature"
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isOk());


        verify(paymentWebhookService)
                .process(any());
    }

    @Test
    void shouldReturnUnauthorizedWhenTimestampIsInvalid()
            throws Exception {

        String payload = """
                {
                    "paymentId": "123",
                    "status": "APPROVED"
                }
                """;

        when(webhookSignatureService.isTimestampValid("invalid"))
                .thenReturn(false);

        mockMvc.perform(
                        post(basePath())
                                .header(
                                        "X-Webhook-Timestamp",
                                        "invalid"
                                )
                                .header(
                                        "X-Webhook-Signature",
                                        "signature"
                                )
                                .contentType("application/json")
                                .content(payload)
                )
                .andExpect(status().isUnauthorized());

        verify(paymentWebhookService, never())
                .process(any());
    }

    @Test
    void shouldReturnUnauthorizedWhenSignatureIsInvalid()
            throws Exception {

        String payload = """
                {
                    "paymentId": "123",
                    "status": "APPROVED"
                }
                """;

        when(webhookSignatureService.isTimestampValid("1723456789"))
                .thenReturn(true);

        when(webhookSignatureService.isValid(
                payload,
                "1723456789",
                "invalid-signature"
        ))
                .thenReturn(false);

        mockMvc.perform(
                        post(basePath())
                                .header(
                                        "X-Webhook-Timestamp",
                                        "1723456789"
                                )
                                .header(
                                        "X-Webhook-Signature",
                                        "invalid-signature"
                                )
                                .contentType("application/json")
                                .content(payload)
                )
                .andExpect(status().isUnauthorized());

        verify(paymentWebhookService, never())
                .process(any());
    }
}