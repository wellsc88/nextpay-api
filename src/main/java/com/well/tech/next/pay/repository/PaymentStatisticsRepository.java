package com.well.tech.next.pay.repository;

import com.well.tech.next.pay.dto.request.payment.PaymentStatisticsFilterRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentStatisticsResponse;

public interface PaymentStatisticsRepository {

    PaymentStatisticsResponse getStatistics(
            PaymentStatisticsFilterRequest filter
    );
}