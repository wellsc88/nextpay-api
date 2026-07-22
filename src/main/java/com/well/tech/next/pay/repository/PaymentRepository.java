package com.well.tech.next.pay.repository;

import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.common.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByCustomerId(UUID customerId);

    List<Payment> findByStatus(PaymentStatus status);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}
