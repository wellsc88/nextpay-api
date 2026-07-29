package com.well.tech.next.pay.dto.response.token;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "RefreshTokenResponse",
        description = "Response payload containing a new access token"
)
public record RefreshTokenResponse(

        @Schema(
                description = "New JWT access token",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
        String accessToken

) {
}