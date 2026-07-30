package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.dto.request.payment.PaymentStatisticsFilterRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentStatisticsResponse;
import com.well.tech.next.pay.repository.PaymentRepository;
import com.well.tech.next.pay.repository.PaymentStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentStatisticsService {

    private final PaymentStatisticsRepository paymentStatisticsRepository;

    @Transactional(readOnly = true)
    public PaymentStatisticsResponse getStatistics(
            PaymentStatisticsFilterRequest filter
    ) {

        log.info(
                "Retrieving payment statistics. filter={}",
                filter
        );

        PaymentStatisticsResponse statistics =
                paymentStatisticsRepository.getStatistics(filter);

        log.info(
                """
                Payment statistics retrieved successfully.
                totalPayments={},
                approvedPayments={},
                totalAmount={},
                approvedAmount={}
                """,
                statistics.totalPayments(),
                statistics.approvedPayments(),
                statistics.totalAmount(),
                statistics.approvedAmount()
        );

        return statistics;
    }
}