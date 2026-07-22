package com.well.tech.next.pay.security;

import com.well.tech.next.pay.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {

        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
        this.expiration = expiration;
    }

    public String generateToken(UUID userId, String email, Role role) {

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("roleType", role.getName())
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + expiration)
                )
                .signWith(secretKey)
                .compact();
    }


    public UUID extractUserId(String token) {

        Claims claims = extractClaims(token);

        return UUID.fromString(
                claims.getSubject()
        );
    }


    public boolean isValid(String token) {

        try {
            extractClaims(token);
            return true;

        } catch (Exception ex) {
            return false;
        }
    }


    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}