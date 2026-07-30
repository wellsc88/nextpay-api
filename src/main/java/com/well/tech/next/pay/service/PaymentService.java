package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentEventType;
import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.common.exceptions.validation.CustomerNotFoundException;
import com.well.tech.next.pay.common.exceptions.validation.InvalidPaymentRetryException;
import com.well.tech.next.pay.common.exceptions.validation.InvalidPaymentStatusTransitionException;
import com.well.tech.next.pay.common.exceptions.validation.PaymentExpiredException;
import com.well.tech.next.pay.domain.PaymentStatusTransition;
import com.well.tech.next.pay.dto.request.payment.CreatePaymentRequest;
import com.well.tech.next.pay.dto.request.payment.PaymentFilterRequest;
import com.well.tech.next.pay.dto.request.payment.UpdatePaymentRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentResponse;
import com.well.tech.next.pay.entity.Customer;
import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.mapper.PaymentMapper;
import com.well.tech.next.pay.repository.CustomerRepository;
import com.well.tech.next.pay.repository.PaymentRepository;
import com.well.tech.next.pay.repository.specification.PaymentSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final PaymentStatusTransitionService paymentStatusTransitionService;
    private final PaymentEventService paymentEventService;
    private final PaymentReferenceService paymentReferenceService;

    @Transactional
    public PaymentResponse create(
            String idempotencyKey,
            CreatePaymentRequest request
    ) {

        log.info(
                "Creating payment. customerId={}, idempotencyKey={}",
                request.customerId(),
                idempotencyKey
        );

        Optional<Payment> existingPayment =
                paymentRepository.findByIdempotencyKey(idempotencyKey);

        if (existingPayment.isPresent()) {
            return paymentMapper.toResponse(existingPayment.get());
        }

        Customer customer = customerRepository
                .findById(request.customerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer id not found: " + request.customerId()
                        )
                );

        Payment payment = paymentMapper.toEntity(
                request,
                customer
        );

        payment.setReference(
                paymentReferenceService.generateReference()
        );

        payment.setExpiresAt(
                LocalDateTime.now().plusMinutes(30)
        );

        payment.setIdempotencyKey(idempotencyKey);

        Payment savedPayment = paymentRepository.save(payment);

        paymentEventService.record(
                savedPayment,
                PaymentEventType.PAYMENT_CREATED,
                "Payment created successfully"
        );

        log.info(
                "Payment created successfully. paymentId={}",
                savedPayment.getId()
        );

        return paymentMapper.toResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse update(
            UUID id,
            UpdatePaymentRequest request
    ) {

        log.info("Updating payment. paymentId={}", id);

        Payment payment = findEntity(id);

        paymentMapper.updateEntity(
                payment,
                request
        );

        Payment updatedPayment = paymentRepository.save(payment);

        paymentEventService.record(
                updatedPayment,
                PaymentEventType.PAYMENT_UPDATED,
                "Payment updated successfully"
        );

        log.info("Payment updated successfully. paymentId={}", id);

        return paymentMapper.toResponse(updatedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse findById(UUID id) {

        return paymentMapper.toResponse(
                findEntity(id)
        );
    }


    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAll(
            PaymentFilterRequest filter,
            Pageable pageable
    ) {

        Specification<Payment> specification =
                PaymentSpecification.filter(filter);

        return paymentRepository
                .findAll(specification, pageable)
                .map(paymentMapper::toResponse);
    }

    @Transactional
    public void delete(UUID id) {

        Payment payment = findEntity(id);

        paymentRepository.delete(payment);
    }

    @Transactional
    public PaymentResponse updateStatus(
            UUID paymentId,
            PaymentStatus targetStatus
    ) {

        log.info(
                "Updating payment status. paymentId={}, targetStatus={}",
                paymentId,
                targetStatus
        );

        Payment payment = findEntity(paymentId);

        validateExpiration(payment);

        PaymentStatus currentStatus = payment.getStatus();

        if (currentStatus == targetStatus) {

            log.info(
                    "Payment already has status {}. paymentId={}",
                    targetStatus,
                    paymentId
            );

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

        Payment updatedPayment = paymentRepository.save(payment);

        paymentStatusHistoryService.record(
                updatedPayment,
                currentStatus,
                targetStatus
        );

        paymentEventService.record(
                updatedPayment,
                PaymentEventType.PAYMENT_STATUS_CHANGED,
                String.format(
                        "Status changed from %s to %s",
                        currentStatus,
                        targetStatus
                )
        );

        log.info(
                "Payment status updated successfully. paymentId={}, fromStatus={}, toStatus={}",
                paymentId,
                currentStatus,
                targetStatus
        );

        return paymentMapper.toResponse(updatedPayment);
    }

    @Transactional
    public void cancel(UUID paymentId) {

        log.info("Cancelling payment. paymentId={}", paymentId);

        Payment payment = findEntity(paymentId);

        validateExpiration(payment);

        PaymentStatus currentStatus = payment.getStatus();
        PaymentStatus newStatus = PaymentStatus.CANCELLED;

        paymentStatusTransitionService.validate(
                currentStatus,
                newStatus
        );

        payment.setStatus(newStatus);

        Payment updatedPayment = paymentRepository.save(payment);

        paymentStatusHistoryService.record(
                updatedPayment,
                currentStatus,
                newStatus
        );

        paymentEventService.record(
                updatedPayment,
                PaymentEventType.PAYMENT_CANCELLED,
                "Payment cancelled successfully"
        );

        log.info(
                "Payment cancelled successfully. paymentId={}",
                paymentId
        );
    }

    @Transactional
    public void refund(UUID paymentId) {

        log.info("Refunding payment. paymentId={}", paymentId);

        Payment payment = findEntity(paymentId);

        PaymentStatus currentStatus = payment.getStatus();
        PaymentStatus newStatus = PaymentStatus.REFUNDED;

        paymentStatusTransitionService.validate(
                currentStatus,
                newStatus
        );

        payment.setStatus(newStatus);

        Payment updatedPayment = paymentRepository.save(payment);

        paymentStatusHistoryService.record(
                updatedPayment,
                currentStatus,
                newStatus
        );

        paymentEventService.record(
                updatedPayment,
                PaymentEventType.PAYMENT_REFUNDED,
                "Payment refunded successfully"
        );

        log.info(
                "Payment refunded successfully. paymentId={}",
                paymentId
        );
    }

    @Transactional
    public PaymentResponse retry(UUID paymentId) {

        log.info("Retrying payment. paymentId={}", paymentId);

        Payment originalPayment = findEntity(paymentId);

        if (originalPayment.getStatus() != PaymentStatus.DECLINED) {

            throw new InvalidPaymentRetryException(
                    originalPayment.getStatus().toString()
            );
        }

        Payment retryPayment = Payment.builder()
                .customer(originalPayment.getCustomer())
                .parentPayment(originalPayment)
                .idempotencyKey(UUID.randomUUID().toString())
                .amount(originalPayment.getAmount())
                .currency(originalPayment.getCurrency())
                .status(PaymentStatus.PENDING)
                .paymentMethod(originalPayment.getPaymentMethod())
                .description(originalPayment.getDescription())
                .build();

        Payment savedRetryPayment = paymentRepository.save(retryPayment);

        paymentEventService.record(
                savedRetryPayment,
                PaymentEventType.PAYMENT_RETRY_CREATED,
                String.format(
                        "Retry payment created from payment %s",
                        originalPayment.getId()
                )
        );

        log.info(
                "Retry payment created successfully. originalPaymentId={}, retryPaymentId={}",
                originalPayment.getId(),
                savedRetryPayment.getId()
        );

        return paymentMapper.toResponse(savedRetryPayment);
    }

    private Payment findEntity(UUID id) {

        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"
                        )
                );
    }

    @Transactional
    public void expire(UUID paymentId) {

        log.info("Expiring payment. paymentId={}", paymentId);

        Payment payment = findEntity(paymentId);

        PaymentStatus currentStatus = payment.getStatus();
        PaymentStatus newStatus = PaymentStatus.EXPIRED;

        paymentStatusTransitionService.validate(
                currentStatus,
                newStatus
        );

        payment.setStatus(newStatus);

        Payment updatedPayment = paymentRepository.save(payment);

        paymentStatusHistoryService.record(
                updatedPayment,
                currentStatus,
                newStatus
        );

        paymentEventService.record(
                updatedPayment,
                PaymentEventType.PAYMENT_EXPIRED,
                "Payment expired successfully"
        );

        log.info(
                "Payment expired successfully. paymentId={}, fromStatus={}, toStatus={}",
                paymentId,
                currentStatus,
                newStatus
        );
    }

    private void validateExpiration(Payment payment) {

        if (payment.getStatus() == PaymentStatus.PENDING
                && payment.getExpiresAt() != null
                && payment.getExpiresAt().isBefore(LocalDateTime.now())) {

            payment.setStatus(PaymentStatus.EXPIRED);

            paymentRepository.save(payment);

            paymentStatusHistoryService.record(
                    payment,
                    PaymentStatus.PENDING,
                    PaymentStatus.EXPIRED
            );

            paymentEventService.record(
                    payment,
                    PaymentEventType.PAYMENT_EXPIRED,
                    "Payment expired automatically"
            );

            throw new PaymentExpiredException(
                    "Payment has expired"
            );
        }
    }

    @Transactional(readOnly = true)
    public PaymentResponse findByReference(String reference) {

        log.info(
                "Finding payment by reference. reference={}",
                reference
        );

        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> {
                    log.warn(
                            "Payment not found. reference={}",
                            reference
                    );

                    return new ResourceNotFoundException(
                            "Payment not found"
                    );
                });

        return paymentMapper.toResponse(payment);
    }
}