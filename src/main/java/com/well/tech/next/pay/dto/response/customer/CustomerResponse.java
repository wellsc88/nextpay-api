package com.well.tech.next.pay.dto.response.customer;

import com.well.tech.next.pay.entity.Customer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        name = "CustomerResponse",
        description = "Response payload containing customer information"
)
public record CustomerResponse(

        @Schema(
                description = "Customer UUID",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,


        @Schema(
                description = "Customer full name",
                example = "João Silva"
        )
        String name,


        @Schema(
                description = "Customer email address",
                example = "joao.silva@email.com"
        )
        String email,


        @Schema(
                description = "Customer identification document",
                example = "12345678900"
        )
        String document,


        @Schema(
                description = "Customer creation date/time",
                example = "2026-07-29T10:30:00"
        )
        LocalDateTime createdAt,


        @Schema(
                description = "Customer last update date/time",
                example = "2026-07-29T10:45:00"
        )
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