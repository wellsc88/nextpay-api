package com.well.tech.next.pay.repository;

import com.well.tech.next.pay.entity.Payment;
import com.well.tech.next.pay.common.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID>,
        JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findByReference(String reference);

    boolean existsByReference(String reference);

}
