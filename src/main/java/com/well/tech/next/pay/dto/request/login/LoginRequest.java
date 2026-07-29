package com.well.tech.next.pay.dto.request.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "LoginRequest",
        description = "Request payload for user authentication"
)
public record LoginRequest(

        @Schema(
                description = "User email address",
                example = "admin@nextpay.com"
        )
        @NotBlank
        @Email
        String email,


        @Schema(
                description = "User password",
                example = "Password@123"
        )
        @NotBlank
        String password
) {
}