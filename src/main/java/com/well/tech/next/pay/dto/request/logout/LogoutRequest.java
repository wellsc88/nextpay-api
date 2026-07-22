package com.well.tech.next.pay.dto.request.logout;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
    @NotBlank
    String refreshToken
){}
