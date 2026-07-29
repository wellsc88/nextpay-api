package com.well.tech.next.pay.dto.request.customer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "CustomerFilterRequest",
        description = "Filters available for searching customers"
)
public record CustomerFilterRequest(

        @Schema(
                description = "Filter customers by name",
                example = "João"
        )
        String name,


        @Schema(
                description = "Filter customers by email",
                example = "joao.silva@email.com"
        )
        String email,


        @Schema(
                description = "Filter customers by document number",
                example = "12345678900"
        )
        String document
) {
}