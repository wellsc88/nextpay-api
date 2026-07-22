package com.well.tech.next.pay.dto.request.role;

import jakarta.validation.constraints.NotNull;

public record UpdateRoleStatusRequest(
    @NotNull
    Boolean enabled
){}
