package com.well.tech.next.pay.common.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(
        name = "ApiError",
        description = "Standard API error response"
)
public record ApiError(

        @Schema(
                description = "HTTP status code",
                example = "400"
        )
        int status,


        @Schema(
                description = "HTTP error type",
                example = "Bad Request"
        )
        String error,


        @Schema(
                description = "Detailed error message",
                example = "Payment status transition is not allowed"
        )
        String message,


        @Schema(
                description = "API endpoint path where the error occurred",
                example = "/api/v1/payments/550e8400-e29b-41d4-a716-446655440000/status"
        )
        String path,


        @Schema(
                description = "Timestamp when the error occurred",
                example = "2026-07-29T17:30:00Z"
        )
        Instant timestamp
) {
}