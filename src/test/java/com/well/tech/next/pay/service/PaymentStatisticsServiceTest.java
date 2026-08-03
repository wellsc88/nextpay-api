package com.well.tech.next.pay.service;

import com.well.tech.next.pay.dto.request.payment.PaymentStatisticsFilterRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentStatisticsResponse;
import com.well.tech.next.pay.repository.PaymentStatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class PaymentStatisticsServiceTest {

    private PaymentStatisticsRepository paymentStatisticsRepository;

    private PaymentStatisticsService paymentStatisticsService;

    @BeforeEach
    void setup() {

        paymentStatisticsRepository =
                Mockito.mock(PaymentStatisticsRepository.class);

        paymentStatisticsService =
                new PaymentStatisticsService(
                        paymentStatisticsRepository
                );
    }

    @Test
    void shouldGetStatisticsSuccessfully() {

        PaymentStatisticsFilterRequest filter =
                Mockito.mock(PaymentStatisticsFilterRequest.class);

        PaymentStatisticsResponse response =
                Mockito.mock(PaymentStatisticsResponse.class);

        when(paymentStatisticsRepository.getStatistics(filter))
                .thenReturn(response);

        PaymentStatisticsResponse result =
                paymentStatisticsService.getStatistics(filter);

        assertNotNull(result);

        verify(paymentStatisticsRepository)
                .getStatistics(filter);
    }
}