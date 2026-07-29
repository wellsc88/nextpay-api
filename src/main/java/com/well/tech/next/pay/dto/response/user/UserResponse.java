package com.well.tech.next.pay.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
        name = "UserResponse",
        description = "Response payload containing user information"
)
public record UserResponse(

        @Schema(
                description = "User UUID",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,


        @Schema(
                description = "User full name",
                example = "João Silva"
        )
        String name,


        @Schema(
                description = "User email address",
                example = "joao.silva@nextpay.com"
        )
        String email

) {
}