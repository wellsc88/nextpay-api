package com.well.tech.next.pay.mapper;

import com.well.tech.next.pay.dto.request.payment.CreatePaymentRequest;
import com.well.tech.next.pay.dto.request.payment.UpdatePaymentRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentResponse;
import com.well.tech.next.pay.entity.Customer;
import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.common.enums.PaymentMethod;
import com.well.tech.next.pay.common.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMapperTest {

    private PaymentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PaymentMapper();
    }

    @Test
    void shouldConvertCreatePaymentRequestToEntity() {

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("Wellington")
                .email("well@test.com")
                .build();

        CreatePaymentRequest request = new CreatePaymentRequest(
                customer.getId(),
                new BigDecimal("100.50"),
                "BRL",
                PaymentMethod.CREDIT_CARD,
                "Payment test"
        );

        Payment payment = mapper.toEntity(request, customer);

        assertThat(payment.getCustomer())
                .isEqualTo(customer);

        assertThat(payment.getAmount())
                .isEqualByComparingTo("100.50");

        assertThat(payment.getCurrency())
                .isEqualTo("BRL");

        assertThat(payment.getPaymentMethod())
                .isEqualTo(PaymentMethod.CREDIT_CARD);

        assertThat(payment.getDescription())
                .isEqualTo("Payment test");
    }

    @Test
    void shouldUpdatePaymentEntityFields() {

        Payment payment = Payment.builder()
                .amount(new BigDecimal("50.00"))
                .currency("USD")
                .paymentMethod(PaymentMethod.PIX)
                .description("Old description")
                .build();

        UpdatePaymentRequest request = new UpdatePaymentRequest(
                new BigDecimal("200.00"),
                "eur",
                PaymentMethod.DEBIT_CARD,
                "Updated payment"
        );

        mapper.updateEntity(payment, request);

        assertThat(payment.getAmount())
                .isEqualByComparingTo("200.00");

        assertThat(payment.getCurrency())
                .isEqualTo("EUR");

        assertThat(payment.getPaymentMethod())
                .isEqualTo(PaymentMethod.DEBIT_CARD);

        assertThat(payment.getDescription())
                .isEqualTo("Updated payment");
    }

    @Test
    void shouldConvertPaymentEntityToResponse() {

        UUID paymentId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        Customer customer = Customer.builder()
                .id(customerId)
                .build();

        Payment payment = Payment.builder()
                .id(paymentId)
                .customer(customer)
                .amount(new BigDecimal("150.00"))
                .currency("BRL")
                .status(PaymentStatus.APPROVED)
                .paymentMethod(PaymentMethod.PIX)
                .description("Test payment")
                .reference("PAY-12345")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        PaymentResponse response = mapper.toResponse(payment);

        assertThat(response.id())
                .isEqualTo(paymentId);

        assertThat(response.customerId())
                .isEqualTo(customerId);

        assertThat(response.amount())
                .isEqualByComparingTo("150.00");

        assertThat(response.currency())
                .isEqualTo("BRL");

        assertThat(response.status())
                .isEqualTo(PaymentStatus.APPROVED);

        assertThat(response.paymentMethod())
                .isEqualTo(PaymentMethod.PIX);

        assertThat(response.description())
                .isEqualTo("Test payment");

        assertThat(response.reference())
                .isEqualTo("PAY-12345");

        assertThat(response.createdAt())
                .isEqualTo(createdAt);

        assertThat(response.updatedAt())
                .isEqualTo(updatedAt);
    }
}