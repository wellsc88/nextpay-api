package com.well.tech.next.pay.mapper;

import com.well.tech.next.pay.common.enums.PaymentEventType;
import com.well.tech.next.pay.dto.response.payment.PaymentEventResponse;
import com.well.tech.next.pay.entity.PaymentEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentEventMapperTest {

    @Test
    void shouldConvertPaymentEventToResponse() {

        UUID id = UUID.randomUUID();

        LocalDateTime createdAt = LocalDateTime.now();

        PaymentEvent event = PaymentEvent.builder()
                .id(id)
                .eventType(PaymentEventType.PAYMENT_CREATED)
                .description("Payment created successfully")
                .createdAt(createdAt)
                .build();

        PaymentEventResponse response =
                PaymentEventMapper.from(event);

        assertThat(response.id())
                .isEqualTo(id);

        assertThat(response.eventType())
                .isEqualTo(PaymentEventType.PAYMENT_CREATED);

        assertThat(response.description())
                .isEqualTo("Payment created successfully");

        assertThat(response.createdAt())
                .isEqualTo(createdAt);
    }
}