package com.well.tech.next.pay.controller;

import com.well.tech.next.pay.dto.request.login.LoginRequest;
import com.well.tech.next.pay.dto.request.logout.LogoutRequest;
import com.well.tech.next.pay.dto.request.token.RefreshTokenRequest;
import com.well.tech.next.pay.dto.response.login.LoginResponse;
import com.well.tech.next.pay.dto.response.token.RefreshTokenResponse;
import com.well.tech.next.pay.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.well.tech.next.pay.config.ApiVersion.API_BASE_PATH;
import static com.well.tech.next.pay.config.ApiVersion.API_VERSION;

@RestController
@RequestMapping(API_BASE_PATH + "/" + API_VERSION + "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                service.login(request)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @RequestBody RefreshTokenRequest request
    ) {

        return ResponseEntity.ok(
                service.refreshToken(request)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody LogoutRequest request) {

        service.logout(request.refreshToken());

        return ResponseEntity.noContent().build();
    }
}