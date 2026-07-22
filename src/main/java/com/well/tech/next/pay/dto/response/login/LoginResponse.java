package com.well.tech.next.pay.dto.response.login;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}