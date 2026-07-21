package com.well.tech.next.pay.dto.request.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must have a maximum of 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 150, message = "Email must have a maximum of 150 characters")
        String email,

        @NotBlank(message = "Document is required")
        @Size(max = 20, message = "Document must have a maximum of 20 characters")
        String document
) {
}
