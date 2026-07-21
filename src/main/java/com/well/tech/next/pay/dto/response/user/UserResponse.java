package com.well.tech.next.pay.dto.response.user;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email
) {}