package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.dto.response.payment.PaymentStatisticsResponse;
import com.well.tech.next.pay.repository.PaymentRepository;
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

    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public PaymentStatisticsResponse getStatistics() {

        log.info("Retrieving payment statistics");

        PaymentStatisticsResponse statistics = new PaymentStatisticsResponse(
                paymentRepository.count(),
                paymentRepository.countByStatus(PaymentStatus.PENDING),
                paymentRepository.countByStatus(PaymentStatus.PROCESSING),
                paymentRepository.countByStatus(PaymentStatus.APPROVED),
                paymentRepository.countByStatus(PaymentStatus.DECLINED),
                paymentRepository.countByStatus(PaymentStatus.CANCELLED),
                paymentRepository.countByStatus(PaymentStatus.REFUNDED),
                paymentRepository.countByStatus(PaymentStatus.EXPIRED),
                money(paymentRepository.sumAmount()),
                money(paymentRepository.sumAmountByStatus(PaymentStatus.APPROVED)),
                money(paymentRepository.sumAmountByStatus(PaymentStatus.REFUNDED))
        );

        return statistics;
    }

    private BigDecimal money(BigDecimal value) {
        return value == null
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : value.setScale(2, RoundingMode.HALF_UP);
    }
}