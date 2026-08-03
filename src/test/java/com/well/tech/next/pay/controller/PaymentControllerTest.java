package com.well.tech.next.pay.controller;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.GlobalExceptionHandler;
import com.well.tech.next.pay.dto.request.payment.CreatePaymentRequest;
import com.well.tech.next.pay.dto.request.payment.UpdatePaymentRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentResponse;
import com.well.tech.next.pay.security.JwtAuthenticationFilter;
import com.well.tech.next.pay.security.JwtService;
import com.well.tech.next.pay.service.*;

import com.well.tech.next.pay.config.ApiVersion;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private PaymentStatusHistoryService paymentStatusHistoryService;

    @MockitoBean
    private PaymentEventService paymentEventService;

    @MockitoBean
    private PaymentTimelineService paymentTimelineService;

    @MockitoBean
    private PaymentStatisticsService paymentStatisticsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String basePath() {

        return ApiVersion.API_BASE_PATH
                + "/"
                + ApiVersion.API_VERSION
                + "/payments";
    }

    private PaymentResponse response(UUID id) {

        return new PaymentResponse(
                id,
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                "BRL",
                PaymentStatus.APPROVED,
                null,
                "Payment test",
                "REF-123",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldFindPaymentByIdSuccessfully()
            throws Exception {

        UUID id = UUID.randomUUID();

        when(paymentService.findById(id))
                .thenReturn(response(id));

        mockMvc.perform(
                        get(basePath() + "/{id}", id)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindAllPaymentsSuccessfully()
            throws Exception {

        when(paymentService.findAll(any(), any()))
                .thenReturn(
                        new PageImpl<>(
                                List.of(
                                        response(UUID.randomUUID())
                                ),
                                PageRequest.of(0,10),
                                1
                        )
                );

        mockMvc.perform(
                        get(basePath())
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeletePaymentSuccessfully()
            throws Exception {

        UUID id = UUID.randomUUID();

        doNothing()
                .when(paymentService)
                .delete(id);

        mockMvc.perform(
                        delete(basePath() + "/{id}", id)
                )
                .andExpect(status().isNoContent());

        verify(paymentService)
                .delete(id);
    }

    @Test
    void shouldGetStatusHistorySuccessfully()
            throws Exception {

        UUID id = UUID.randomUUID();

        when(paymentStatusHistoryService.findByPaymentId(id))
                .thenReturn(List.of());

        mockMvc.perform(
                        get(
                                basePath()
                                        + "/{id}/status-history",
                                id
                        )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldCancelPaymentSuccessfully()
            throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(
                        post(
                                basePath()
                                        + "/{id}/cancel",
                                id
                        )
                )
                .andExpect(status().isNoContent());


        verify(paymentService)
                .cancel(id);
    }

    @Test
    void shouldExpirePaymentSuccessfully()
            throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(
                        post(
                                basePath()
                                        + "/{id}/expire",
                                id
                        )
                )
                .andExpect(status().isNoContent());


        verify(paymentService)
                .expire(id);
    }

    @Test
    void shouldGetEventsSuccessfully()
            throws Exception {

        UUID id = UUID.randomUUID();

        when(paymentEventService.findByPaymentId(id))
                .thenReturn(List.of());

        mockMvc.perform(
                        get(
                                basePath()
                                        + "/{id}/events",
                                id
                        )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetTimelineSuccessfully()
            throws Exception {

        UUID id = UUID.randomUUID();

        when(paymentTimelineService.findTimeline(id))
                .thenReturn(List.of());

        mockMvc.perform(
                        get(
                                basePath()
                                        + "/{id}/timeline",
                                id
                        )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindPaymentByReferenceSuccessfully()
            throws Exception {

        when(paymentService.findByReference("REF-123"))
                .thenReturn(
                        response(UUID.randomUUID())
                );

        mockMvc.perform(
                        get(
                                basePath()
                                        + "/reference/REF-123"
                        )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetStatisticsSuccessfully()
            throws Exception {

        when(paymentStatisticsService.getStatistics(any()))
                .thenReturn(null);

        mockMvc.perform(
                        get(
                                basePath()
                                        + "/statistics"
                        )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreatePaymentSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();

        when(paymentService.create(
                eq("idem-123"),
                any(CreatePaymentRequest.class)
        )).thenReturn(response(id));

        mockMvc.perform(
                        post(basePath())
                                .header("Idempotency-Key", "idem-123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "customerId": "550e8400-e29b-41d4-a716-446655440000",
                              "amount": 100.00,
                              "currency": "BRL",
                              "paymentMethod": "CREDIT_CARD",
                              "description": "Payment test"
                            }
                            """)
                )
                .andExpect(status().isCreated());

        verify(paymentService)
                .create(
                        eq("idem-123"),
                        any(CreatePaymentRequest.class)
                );
    }

    @Test
    void shouldUpdatePaymentSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();

        when(paymentService.update(
                eq(id),
                any(UpdatePaymentRequest.class)
        )).thenReturn(response(id));

        mockMvc.perform(
                        patch(basePath() + "/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "amount": 150.00,
                                "currency": "BRL",
                                "paymentMethod": "CREDIT_CARD",
                                "description": "Payment test"
                            }
                            """)
                )
                .andExpect(status().isOk());

        verify(paymentService)
                .update(
                        eq(id),
                        any(UpdatePaymentRequest.class)
                );
    }

    @Test
    void shouldUpdatePaymentStatusSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();

        when(paymentService.updateStatus(
                eq(id),
                eq(PaymentStatus.APPROVED)))
                .thenReturn(response(id));

        mockMvc.perform(
                        patch(basePath() + "/{id}/status", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                  "status":"APPROVED"
                                }
                                """)
                )
                .andExpect(status().isOk());

        verify(paymentService)
                .updateStatus(id, PaymentStatus.APPROVED);
    }

    @Test
    void shouldRefundPaymentSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();

        doNothing()
                .when(paymentService)
                .refund(id);

        mockMvc.perform(
                        post(basePath() + "/{id}/refund", id)
                )
                .andExpect(status().isNoContent());

        verify(paymentService)
                .refund(id);
    }

    @Test
    void shouldRetryPaymentSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();

        when(paymentService.retry(id))
                .thenReturn(response(id));

        mockMvc.perform(
                        post(basePath() + "/{id}/retry", id)
                )
                .andExpect(status().isCreated());

        verify(paymentService)
                .retry(id);
    }
}