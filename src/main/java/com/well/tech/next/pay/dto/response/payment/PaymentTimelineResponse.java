package com.well.tech.next.pay.dto.response.payment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "PaymentTimelineResponse",
        description = "Response containing a payment timeline entry"
)
public record PaymentTimelineResponse(

        @Schema(
                description = "Timeline entry type",
                example = "EVENT"
        )
        String type,

        @Schema(
                description = "Timeline entry description",
                example = "Payment created successfully"
        )
        String description,

        @Schema(
                description = "Date and time when the timeline entry was created",
                example = "2026-07-30T18:45:12"
        )
        LocalDateTime createdAt

) {
}