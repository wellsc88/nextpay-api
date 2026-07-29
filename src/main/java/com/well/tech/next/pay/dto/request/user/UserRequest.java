package com.well.tech.next.pay.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "UserRequest",
        description = "Request payload for user registration"
)
public record UserRequest(

        @Schema(
                description = "User full name",
                example = "João Silva"
        )
        @NotBlank(message = "name is required")
        String name,


        @Schema(
                description = "User email address",
                example = "joao.silva@nextpay.com"
        )
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,


        @Schema(
                description = "User password",
                example = "Password@123",
                format = "password"
        )
        @NotBlank(message = "Password is required")
        String password

) {
}