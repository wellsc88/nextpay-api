package com.well.tech.next.pay.dto.request.token;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank
    String refreshToken
){}
