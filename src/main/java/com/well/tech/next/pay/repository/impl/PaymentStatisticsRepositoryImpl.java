package com.well.tech.next.pay.repository.impl;

import com.well.tech.next.pay.common.enums.PaymentStatus;
import com.well.tech.next.pay.dto.request.payment.PaymentStatisticsFilterRequest;
import com.well.tech.next.pay.dto.response.payment.PaymentStatisticsResponse;
import com.well.tech.next.pay.repository.PaymentStatisticsRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import com.well.tech.next.pay.entity.Payment;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PaymentStatisticsRepositoryImpl
        implements PaymentStatisticsRepository {

    private final EntityManager entityManager;

    @Override
    public PaymentStatisticsResponse getStatistics(
            PaymentStatisticsFilterRequest filter
    ) {

        log.debug(
                "Retrieving payment statistics with filters. filter={}",
                filter
        );

        return new PaymentStatisticsResponse(

                count(filter, null),

                countByStatus(filter, PaymentStatus.PENDING),
                countByStatus(filter, PaymentStatus.PROCESSING),
                countByStatus(filter, PaymentStatus.APPROVED),
                countByStatus(filter, PaymentStatus.DECLINED),
                countByStatus(filter, PaymentStatus.CANCELLED),
                countByStatus(filter, PaymentStatus.REFUNDED),
                countByStatus(filter, PaymentStatus.EXPIRED),

                sum(filter, null),
                sumByStatus(filter, PaymentStatus.APPROVED),
                sumByStatus(filter, PaymentStatus.REFUNDED)

        );
    }

    private long count(
            PaymentStatisticsFilterRequest filter,
            PaymentStatus status
    ) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> query = cb.createQuery(Long.class);

        Root<Payment> root = query.from(Payment.class);

        List<Predicate> predicates = buildPredicates(
                filter,
                status,
                root,
                cb
        );

        query.select(cb.count(root));

        query.where(
                predicates.toArray(new Predicate[0])
        );

        return entityManager
                .createQuery(query)
                .getSingleResult();
    }

    private BigDecimal sum(
            PaymentStatisticsFilterRequest filter,
            PaymentStatus status
    ) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<BigDecimal> query =
                cb.createQuery(BigDecimal.class);

        Root<Payment> root = query.from(Payment.class);

        List<Predicate> predicates = buildPredicates(
                filter,
                status,
                root,
                cb
        );

        query.select(
                cb.coalesce(
                        cb.sum(root.get("amount")),
                        BigDecimal.ZERO
                )
        );

        query.where(
                predicates.toArray(new Predicate[0])
        );

        return entityManager
                .createQuery(query)
                .getSingleResult()
                .setScale(2, RoundingMode.HALF_UP);
    }

    private List<Predicate> buildPredicates(
            PaymentStatisticsFilterRequest filter,
            PaymentStatus status,
            Root<Payment> root,
            CriteriaBuilder cb
    ) {

        List<Predicate> predicates = new ArrayList<>();

        if (filter.from() != null) {
            predicates.add(
                    cb.greaterThanOrEqualTo(
                            root.get("createdAt"),
                            filter.from().atStartOfDay()
                    )
            );
        }

        if (filter.to() != null) {
            predicates.add(
                    cb.lessThanOrEqualTo(
                            root.get("createdAt"),
                            filter.to().atTime(23, 59, 59)
                    )
            );
        }

        if (filter.paymentMethod() != null) {
            predicates.add(
                    cb.equal(
                            root.get("paymentMethod"),
                            filter.paymentMethod()
                    )
            );
        }

        if (filter.currency() != null &&
                !filter.currency().isBlank()) {

            predicates.add(
                    cb.equal(
                            root.get("currency"),
                            filter.currency().toUpperCase()
                    )
            );
        }

        if (filter.customerId() != null) {
            predicates.add(
                    cb.equal(
                            root.get("customer").get("id"),
                            filter.customerId()
                    )
            );
        }

        if (status != null) {
            predicates.add(
                    cb.equal(
                            root.get("status"),
                            status
                    )
            );
        } else if (filter.status() != null) {
            predicates.add(
                    cb.equal(
                            root.get("status"),
                            filter.status()
                    )
            );
        }

        return predicates;
    }

    private long countByStatus(
            PaymentStatisticsFilterRequest filter,
            PaymentStatus status
    ) {

        if (filter.status() != null &&
                filter.status() != status) {

            return 0;
        }

        return count(filter, status);
    }

    private BigDecimal sumByStatus(
            PaymentStatisticsFilterRequest filter,
            PaymentStatus status
    ) {

        if (filter.status() != null &&
                filter.status() != status) {

            return BigDecimal.ZERO;
        }

        return sum(filter, status);
    }
}