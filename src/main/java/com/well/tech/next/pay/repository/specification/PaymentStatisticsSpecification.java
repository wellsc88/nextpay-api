package com.well.tech.next.pay.repository.specification;

import com.well.tech.next.pay.dto.request.payment.PaymentStatisticsFilterRequest;
import com.well.tech.next.pay.entity.Payment;
import org.springframework.data.jpa.domain.Specification;

public final class PaymentStatisticsSpecification {

    private PaymentStatisticsSpecification() {
    }

    public static Specification<Payment> filter(
            PaymentStatisticsFilterRequest filter
    ) {
        return (root, query, criteriaBuilder) -> {

            var predicate = criteriaBuilder.conjunction();

            if (filter.from() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                filter.from().atStartOfDay()
                        )
                );
            }

            if (filter.to() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("createdAt"),
                                filter.to().atTime(23, 59, 59)
                        )
                );
            }

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

            return predicate;
        };
    }
}