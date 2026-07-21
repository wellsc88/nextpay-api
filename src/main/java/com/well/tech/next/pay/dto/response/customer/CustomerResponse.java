package com.well.tech.next.pay.dto.response.customer;

import com.well.tech.next.pay.entity.Customer;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        String document,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getDocument(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}