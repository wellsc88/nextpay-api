package com.well.tech.next.pay.dto.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "UpdateRoleStatusRequest",
        description = "Request payload for enabling or disabling a user account"
)
public record UpdateRoleStatusRequest(

        @Schema(
                description = "Defines whether the user account is enabled",
                example = "true"
        )
        @NotNull
        Boolean enabled
) {
}