package com.well.tech.next.pay.service;

import com.well.tech.next.pay.repository.PaymentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentReferenceServiceTest {

    private PaymentRepository paymentRepository;

    private PaymentReferenceService service;

    @BeforeEach
    void setup() {

        paymentRepository =
                mock(PaymentRepository.class);

        service =
                new PaymentReferenceService(
                        paymentRepository
                );
    }

    @Test
    void shouldGeneratePaymentReferenceSuccessfully() {

        when(paymentRepository.existsByReference(anyString()))
                .thenReturn(false);

        String reference =
                service.generateReference();

        assertNotNull(reference);

        assertTrue(
                reference.startsWith("PAY-")
        );

        assertEquals(
                19,
                reference.length()
        );

        verify(paymentRepository)
                .existsByReference(anyString());
    }

    @Test
    void shouldRetryWhenReferenceAlreadyExists() {
        when(paymentRepository.existsByReference(anyString()))
                .thenReturn(
                        true,
                        false
                );

        String reference =
                service.generateReference();

        assertNotNull(reference);

        assertTrue(
                reference.startsWith("PAY-")
        );

        verify(
                paymentRepository,
                times(2)
        )
                .existsByReference(anyString());
    }
}