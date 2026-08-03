package com.well.tech.next.pay.mapper;

import com.well.tech.next.pay.dto.response.payment.PaymentStatusHistoryResponse;
import com.well.tech.next.pay.entity.PaymentStatusHistory;
import com.well.tech.next.pay.common.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentStatusHistoryMapperTest {

    private PaymentStatusHistoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PaymentStatusHistoryMapper();
    }

    @Test
    void shouldConvertPaymentStatusHistoryToResponse() {

        UUID id = UUID.randomUUID();

        LocalDateTime createdAt = LocalDateTime.now();

        PaymentStatusHistory history = PaymentStatusHistory.builder()
                .id(id)
                .fromStatus(PaymentStatus.PENDING)
                .toStatus(PaymentStatus.APPROVED)
                .createdAt(createdAt)
                .build();

        PaymentStatusHistoryResponse response =
                mapper.toResponse(history);

        assertThat(response.id())
                .isEqualTo(id);

        assertThat(response.fromStatus())
                .isEqualTo(PaymentStatus.PENDING);

        assertThat(response.toStatus())
                .isEqualTo(PaymentStatus.APPROVED);

        assertThat(response.createdAt())
                .isEqualTo(createdAt);
    }
}