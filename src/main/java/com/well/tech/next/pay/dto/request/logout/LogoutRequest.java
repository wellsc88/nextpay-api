package com.well.tech.next.pay.dto.request.logout;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "LogoutRequest",
        description = "Request payload for user logout"
)
public record LogoutRequest(

        @Schema(
                description = "Refresh token to invalidate the user session",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
        @NotBlank
        String refreshToken
) {
}