package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.exceptions.auth.InvalidCredentialsException;
import com.well.tech.next.pay.common.exceptions.auth.UserDisabledException;
import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.dto.request.login.LoginRequest;
import com.well.tech.next.pay.dto.request.token.RefreshTokenRequest;
import com.well.tech.next.pay.dto.response.login.LoginResponse;
import com.well.tech.next.pay.dto.response.token.RefreshTokenResponse;
import com.well.tech.next.pay.entity.RefreshToken;
import com.well.tech.next.pay.entity.User;
import com.well.tech.next.pay.repository.UserRepository;
import com.well.tech.next.pay.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public LoginResponse login(LoginRequest request) {

        log.info(
                "Login attempt. Email={}",
                request.email()
        );

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

        } catch (BadCredentialsException ex) {

            log.warn(
                    "Invalid credentials. Email={}",
                    request.email()
            );

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );

        } catch (DisabledException ex) {

            log.warn(
                    "Disabled user attempted login. Email={}",
                    request.email()
            );

            throw new UserDisabledException(
                    "User account is disabled"
            );

        } catch (AuthenticationException ex) {

            log.warn(
                    "Authentication failed. Email={}",
                    request.email()
            );

            throw new InvalidCredentialsException(
                    "Authentication failed"
            );
        }

        User user = repository.findByEmail(request.email())
                .orElseThrow(() -> {

                    log.warn(
                            "Authenticated user not found. Email={}",
                            request.email()
                    );

                    return new ResourceNotFoundException(
                            "User not found"
                    );
                });

        String accessToken = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        RefreshToken refreshToken =
                refreshTokenService.createOrUpdate(user);

        log.info(
                "Login successful. UserId={}, Email={}",
                user.getId(),
                user.getEmail()
        );

        return new LoginResponse(
                accessToken,
                refreshToken.getToken()
        );
    }

    public RefreshTokenResponse refreshToken(
            RefreshTokenRequest request
    ) {

        log.info("Refreshing access token");

        RefreshToken refreshToken =
                refreshTokenService.findByToken(
                        request.refreshToken()
                );

        refreshTokenService.verifyExpiration(
                refreshToken
        );

        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        log.info(
                "Access token refreshed successfully. UserId={}",
                user.getId()
        );

        return new RefreshTokenResponse(
                accessToken
        );
    }

    @Transactional
    public void logout(String refreshToken) {

        log.info("User logout requested");

        refreshTokenService.deleteByToken(refreshToken);

        log.info("User logout completed successfully");
    }
}