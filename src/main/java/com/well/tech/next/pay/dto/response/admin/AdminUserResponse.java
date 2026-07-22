package com.well.tech.next.pay.dto.response.admin;

import java.util.UUID;

public record AdminUserResponse(
    UUID id,
    String name,
    String email,
    String role,
    boolean enabled
){}
