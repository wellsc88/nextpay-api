package com.well.tech.next.pay.dto.response.login;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "LoginResponse",
        description = "Response payload containing authentication tokens"
)
public record LoginResponse(

        @Schema(
                description = "JWT access token used to authenticate API requests",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
        String accessToken,


        @Schema(
                description = "Refresh token used to generate a new access token",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
        String refreshToken

) {
}