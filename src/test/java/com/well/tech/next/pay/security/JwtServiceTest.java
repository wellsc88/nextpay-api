package com.well.tech.next.pay.security;

import com.well.tech.next.pay.entity.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final String secret =
            "c2VjdXJlLXNlY3JldC1rZXktdGhhdC1tdXN0LWJlLWxvbmctZW5vdWdoLWZvci1qd3Qtc2lnbmluZw==";

    private final JwtService jwtService =
            new JwtService(
                    secret,
                    3600000
            );

    @Test
    void shouldGenerateValidToken() {

        UUID userId = UUID.randomUUID();

        Role role =
                Role.builder()
                        .id(UUID.randomUUID())
                        .name("ADMIN")
                        .build();

        String token =
                jwtService.generateToken(
                        userId,
                        "admin@test.com",
                        role
                );

        assertThat(token)
                .isNotBlank();

        assertThat(jwtService.isValid(token))
                .isTrue();
    }

    @Test
    void shouldExtractUserIdFromToken() {

        UUID userId = UUID.randomUUID();

        Role role =
                Role.builder()
                        .id(UUID.randomUUID())
                        .name("USER")
                        .build();

        String token =
                jwtService.generateToken(
                        userId,
                        "user@test.com",
                        role
                );

        UUID extractedId =
                jwtService.extractUserId(token);

        assertThat(extractedId)
                .isEqualTo(userId);
    }

    @Test
    void shouldExtractClaimsFromToken() {

        UUID userId = UUID.randomUUID();

        Role role =
                Role.builder()
                        .id(UUID.randomUUID())
                        .name("ADMIN")
                        .build();

        String token =
                jwtService.generateToken(
                        userId,
                        "admin@test.com",
                        role
                );

        Claims claims =
                jwtService.extractClaims(token);

        assertThat(claims.getSubject())
                .isEqualTo(userId.toString());

        assertThat(claims.get("email"))
                .isEqualTo("admin@test.com");

        assertThat(claims.get("roleType"))
                .isEqualTo("ADMIN");
    }

    @Test
    void shouldReturnFalseWhenTokenIsInvalid() {

        String invalidToken =
                "invalid.jwt.token";

        boolean valid =
                jwtService.isValid(invalidToken);

        assertThat(valid)
                .isFalse();
    }
}