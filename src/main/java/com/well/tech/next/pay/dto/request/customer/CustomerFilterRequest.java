package com.well.tech.next.pay.dto.request.customer;

public record CustomerFilterRequest(
        String name,
        String email,
        String document
) {
}