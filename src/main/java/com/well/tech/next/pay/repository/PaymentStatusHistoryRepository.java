package com.well.tech.next.pay.repository;

import com.well.tech.next.pay.entity.PaymentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentStatusHistoryRepository
        extends JpaRepository<PaymentStatusHistory, UUID> {

    List<PaymentStatusHistory> findByPaymentIdOrderByCreatedAtAsc(
            UUID paymentId
    );
}