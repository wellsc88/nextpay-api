package com.well.tech.next.pay.dto.request.token;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "RefreshTokenRequest",
        description = "Request payload for generating a new access token using a refresh token"
)
public record RefreshTokenRequest(

        @Schema(
                description = "Valid refresh token issued during authentication",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
        @NotBlank
        String refreshToken

) {
}