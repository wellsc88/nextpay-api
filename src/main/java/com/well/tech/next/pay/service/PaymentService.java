package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.common.exceptions.validation.CustomerNotFoundException;
import com.well.tech.next.pay.common.exceptions.validation.InvalidPaymentStatusTransitionException;
import com.well.tech.next.pay.common.exceptions.validation.PaymentNotFoundException;
import com.well.tech.next.pay.domain.PaymentStatusTransition;
import com.well.tech.next.pay.dto.request.payment.CreatePaymentRequest;
import com.well.tech.next.pay.dto.request.payment.UpdatePaymentRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentResponse;
import com.well.tech.next.pay.entity.Customer;
import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.mapper.PaymentMapper;
import com.well.tech.next.pay.repository.CustomerRepository;
import com.well.tech.next.pay.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentStatusHistoryService paymentStatusHistoryService;

    @Transactional
    public PaymentResponse create(
            String idempotencyKey,
            CreatePaymentRequest request
    ) {
        Optional<Payment> existingPayment =
                paymentRepository.findByIdempotencyKey(
                        idempotencyKey
                );

        if (existingPayment.isPresent()) {
            return paymentMapper.toResponse(
                    existingPayment.get()
            );
        }

        Customer customer = customerRepository
                .findById(request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer id not found: " + request.customerId()
                ));

        Payment payment = paymentMapper.toEntity(
                request,
                customer
        );

        payment.setIdempotencyKey(idempotencyKey);

        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse update(
            UUID id,
            UpdatePaymentRequest request
    ) {

        log.info("Updating payment with id: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Payment not found with id: {}", id);

                    return new ResourceNotFoundException(
                            "Payment not found"
                    );
                });

        paymentMapper.updateEntity(payment, request);

        log.info(
                "Payment updated successfully with id: {}",
                id
        );

        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse findById(UUID id) {

        log.info("Finding payment by id: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Payment not found with id: {}", id);

                    return new ResourceNotFoundException(
                            "Payment not found"
                    );
                });

        return paymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findAll() {

        log.info("Finding all payments");

        List<PaymentResponse> payments = paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();

        log.info("Found {} payments", payments.size());

        return payments;
    }

    @Transactional
    public void delete(UUID id) {

        log.info("Deleting payment with id: {}", id);

        if (!paymentRepository.existsById(id)) {
            log.warn("Payment not found with id: {}", id);

            throw new ResourceNotFoundException(
                    "Payment not found"
            );
        }

        paymentRepository.deleteById(id);

        log.info(
                "Payment deleted successfully with id: {}",
                id
        );
    }

    @Transactional
    public PaymentResponse updateStatus(
            UUID paymentId,
            PaymentStatus targetStatus
    ) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(paymentId)
                );

        PaymentStatus currentStatus = payment.getStatus();

        if (currentStatus == targetStatus) {
            return paymentMapper.toResponse(payment);
        }

        if (!PaymentStatusTransition.isAllowed(
                currentStatus,
                targetStatus
        )) {
            throw new InvalidPaymentStatusTransitionException(
                    currentStatus,
                    targetStatus
            );
        }

        payment.setStatus(targetStatus);

        paymentStatusHistoryService.record(
                payment,
                currentStatus,
                targetStatus
        );

        return paymentMapper.toResponse(
                paymentRepository.save(payment)
        );
    }
}