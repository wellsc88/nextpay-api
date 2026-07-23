package com.well.tech.next.pay.repository.specification;

import com.well.tech.next.pay.dto.request.customer.CustomerFilterRequest;
import com.well.tech.next.pay.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

public final class CustomerSpecification {

    private CustomerSpecification() {
    }

    public static Specification<Customer> filter(
            CustomerFilterRequest filter
    ) {
        return (root, query, criteriaBuilder) -> {

            var predicate = criteriaBuilder.conjunction();

            if (filter.name() != null &&
                    !filter.name().isBlank()) {

                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("name")
                                ),
                                "%" + filter.name().toLowerCase() + "%"
                        )
                );
            }

            if (filter.email() != null &&
                    !filter.email().isBlank()) {

                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("email")
                                ),
                                "%" + filter.email().toLowerCase() + "%"
                        )
                );
            }

            if (filter.document() != null &&
                    !filter.document().isBlank()) {

                predicate = criteriaBuilder.equal(
                        root.get("document"),
                        filter.document()
                );
            }

            return predicate;
        };
    }
}