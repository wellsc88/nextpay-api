package com.well.tech.next.pay.dto.request.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "UpdateCustomerRequest",
        description = "Request payload for updating customer information"
)
public record UpdateCustomerRequest(

        @Schema(
                description = "Customer full name",
                example = "João Silva"
        )
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must have a maximum of 100 characters")
        String name,


        @Schema(
                description = "Customer email address",
                example = "joao.silva@email.com"
        )
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 150, message = "Email must have a maximum of 150 characters")
        String email,


        @Schema(
                description = "Customer identification document",
                example = "12345678900"
        )
        @NotBlank(message = "Document is required")
        @Size(max = 20, message = "Document must have a maximum of 20 characters")
        String document
) {
}