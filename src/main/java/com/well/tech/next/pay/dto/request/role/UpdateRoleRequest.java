package com.well.tech.next.pay.dto.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "UpdateRoleRequest",
        description = "Request payload for updating user role"
)
public record UpdateRoleRequest(

        @Schema(
                description = "New role assigned to the user",
                example = "ADMIN"
        )
        @NotNull
        String role
) {
}