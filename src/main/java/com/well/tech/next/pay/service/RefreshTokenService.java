package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.entity.RefreshToken;
import com.well.tech.next.pay.entity.User;
import com.well.tech.next.pay.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    public RefreshToken createOrUpdate(User user) {

        log.info(
                "Creating or updating refresh token. UserId={}",
                user.getId()
        );

        return repository.findByUser(user)
                .map(this::updateToken)
                .orElseGet(() -> createToken(user));
    }

    private RefreshToken updateToken(RefreshToken refreshToken) {

        log.info(
                "Updating refresh token. UserId={}",
                refreshToken.getUser().getId()
        );

        refreshToken.setToken(
                UUID.randomUUID().toString()
        );

        refreshToken.setExpiresAt(
                Instant.now()
                        .plusMillis(refreshExpiration)
        );

        return repository.save(refreshToken);
    }

    private RefreshToken createToken(User user) {

        log.info(
                "Creating new refresh token. UserId={}",
                user.getId()
        );

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(
                        Instant.now()
                                .plusMillis(refreshExpiration)
                )
                .build();

        return repository.save(refreshToken);
    }

    public RefreshToken findByToken(String token) {

        log.debug("Searching refresh token");

        return repository.findByToken(token)
                .orElseThrow(() -> {

                    log.warn(
                            "Refresh token not found"
                    );

                    return new ResourceNotFoundException(
                            "Refresh token not found"
                    );
                });
    }

    public void verifyExpiration(RefreshToken refreshToken) {

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {

            log.warn(
                    "Expired refresh token. UserId={}",
                    refreshToken.getUser().getId()
            );

            repository.delete(refreshToken);

            throw new ResourceNotFoundException(
                    "Refresh token expired"
            );
        }
    }

    @Transactional
    public void deleteByToken(String token) {

        log.info("Deleting refresh token");

        RefreshToken refreshToken = findByToken(token);

        repository.delete(refreshToken);

        log.info(
                "Refresh token deleted. UserId={}",
                refreshToken.getUser().getId()
        );
    }
}