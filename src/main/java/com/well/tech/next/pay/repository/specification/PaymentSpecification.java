package com.well.tech.next.pay.repository.specification;

import com.well.tech.next.pay.dto.request.payment.PaymentFilterRequest;
import com.well.tech.next.pay.entity.Payment;
import org.springframework.data.jpa.domain.Specification;

public final class PaymentSpecification {

    private PaymentSpecification() {
    }

    public static Specification<Payment> filter(
            PaymentFilterRequest filter
    ) {
        return (root, query, criteriaBuilder) -> {

            var predicate = criteriaBuilder.conjunction();

            if (filter.status() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("status"),
                                filter.status()
                        )
                );
            }

            if (filter.paymentMethod() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("paymentMethod"),
                                filter.paymentMethod()
                        )
                );
            }

            if (filter.currency() != null &&
                    !filter.currency().isBlank()) {

                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("currency"),
                                filter.currency().toUpperCase()
                        )
                );
            }

            if (filter.customerId() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("customer").get("id"),
                                filter.customerId()
                        )
                );
            }

            if (filter.minAmount() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("amount"),
                                filter.minAmount()
                        )
                );
            }

            if (filter.maxAmount() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("amount"),
                                filter.maxAmount()
                        )
                );
            }

            if (filter.createdAtFrom() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                filter.createdAtFrom()
                        )
                );
            }

            if (filter.createdAtTo() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("createdAt"),
                                filter.createdAtTo()
                        )
                );
            }

            return predicate;
        };
    }
}