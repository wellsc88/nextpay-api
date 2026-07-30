package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.common.exceptions.validation.InvalidPaymentStatusTransitionException;
import org.springframework.stereotype.Service;

@Service
public class PaymentStatusTransitionService {

    public void validate(
            PaymentStatus currentStatus,
            PaymentStatus newStatus
    ) {
        if (currentStatus == newStatus) {
            return;
        }

        boolean allowed = switch (currentStatus) {

            case PENDING ->
                    newStatus == PaymentStatus.PROCESSING
                            || newStatus == PaymentStatus.CANCELLED
                            || newStatus == PaymentStatus.EXPIRED;

            case PROCESSING ->
                    newStatus == PaymentStatus.APPROVED
                            || newStatus == PaymentStatus.DECLINED
                            || newStatus == PaymentStatus.CANCELLED;

            case APPROVED ->
                    newStatus == PaymentStatus.REFUNDED;

            case DECLINED,
                 CANCELLED,
                 REFUNDED,
                 EXPIRED ->
                    false;
        };

        if (!allowed) {
            throw new InvalidPaymentStatusTransitionException(
                    currentStatus,
                    newStatus
            );
        }
    }
}