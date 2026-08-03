package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.exceptions.auth.InvalidCredentialsException;
import com.well.tech.next.pay.common.exceptions.auth.UserDisabledException;
import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.dto.request.login.LoginRequest;
import com.well.tech.next.pay.dto.request.token.RefreshTokenRequest;
import com.well.tech.next.pay.dto.response.login.LoginResponse;
import com.well.tech.next.pay.dto.response.token.RefreshTokenResponse;
import com.well.tech.next.pay.entity.RefreshToken;
import com.well.tech.next.pay.entity.Role;
import com.well.tech.next.pay.entity.User;
import com.well.tech.next.pay.repository.UserRepository;
import com.well.tech.next.pay.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.security.authentication.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthenticationManager authenticationManager;
    private UserRepository repository;
    private JwtService jwtService;
    private RefreshTokenService refreshTokenService;

    private AuthService authService;

    @BeforeEach
    void setup() {

        authenticationManager =
                Mockito.mock(AuthenticationManager.class);

        repository =
                Mockito.mock(UserRepository.class);

        jwtService =
                Mockito.mock(JwtService.class);

        refreshTokenService =
                Mockito.mock(RefreshTokenService.class);

        authService =
                new AuthService(
                        authenticationManager,
                        repository,
                        jwtService,
                        refreshTokenService
                );
    }

    private User user() {

        return User.builder()
                .id(UUID.randomUUID())
                .email("test@email.com")
                .role(
                        Role.builder()
                                .name("USER")
                                .build()
                )
                .build();
    }

    @Test
    void shouldLoginSuccessfully() {

        User user = user();

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token("refresh-token")
                        .user(user)
                        .build();

        LoginRequest request =
                new LoginRequest(
                        "test@email.com",
                        "123456"
                );

        when(repository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(
                any(),
                any(),
                any()
        ))
                .thenReturn("access-token");

        when(refreshTokenService.createOrUpdate(user))
                .thenReturn(refreshToken);

        LoginResponse response =
                authService.login(request);

        assertEquals(
                "access-token",
                response.accessToken()
        );

        assertEquals(
                "refresh-token",
                response.refreshToken()
        );

        verify(authenticationManager)
                .authenticate(any());
    }

    @Test
    void shouldThrowInvalidCredentialsWhenPasswordIsWrong() {

        LoginRequest request =
                new LoginRequest(
                        "test@email.com",
                        "wrong"
                );

        doThrow(
                new BadCredentialsException("invalid")
        )
                .when(authenticationManager)
                .authenticate(any());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );
    }

    @Test
    void shouldThrowUserDisabledExceptionWhenUserDisabled() {

        LoginRequest request =
                new LoginRequest(
                        "test@email.com",
                        "123456"
                );

        doThrow(
                new DisabledException("disabled")
        )
                .when(authenticationManager)
                .authenticate(any());

        assertThrows(
                UserDisabledException.class,
                () -> authService.login(request)
        );
    }

    @Test
    void shouldThrowInvalidCredentialsWhenAuthenticationFails() {

        LoginRequest request =
                new LoginRequest(
                        "test@email.com",
                        "123456"
                );

        doThrow(
                new AuthenticationServiceException("error")
        )
                .when(authenticationManager)
                .authenticate(any());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );
    }

    @Test
    void shouldThrowExceptionWhenAuthenticatedUserDoesNotExist() {

        LoginRequest request =
                new LoginRequest(
                        "test@email.com",
                        "123456"
                );

        when(repository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> authService.login(request)
        );
    }

    @Test
    void shouldRefreshTokenSuccessfully() {

        User user = user();

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token("refresh")
                        .user(user)
                        .build();

        RefreshTokenRequest request =
                new RefreshTokenRequest(
                        "refresh"
                );

        when(refreshTokenService.findByToken("refresh"))
                .thenReturn(refreshToken);

        when(jwtService.generateToken(
                any(),
                any(),
                any()
        ))
                .thenReturn("new-access-token");

        RefreshTokenResponse response =
                authService.refreshToken(request);

        assertEquals(
                "new-access-token",
                response.accessToken()
        );

        verify(refreshTokenService)
                .verifyExpiration(refreshToken);
    }

    @Test
    void shouldLogoutSuccessfully() {

        authService.logout("refresh-token");

        verify(refreshTokenService)
                .deleteByToken(
                        "refresh-token"
                );
    }
}