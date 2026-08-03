package com.well.tech.next.pay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.well.tech.next.pay.config.ApiVersion;
import com.well.tech.next.pay.dto.request.login.LoginRequest;
import com.well.tech.next.pay.dto.request.logout.LogoutRequest;
import com.well.tech.next.pay.dto.request.token.RefreshTokenRequest;
import com.well.tech.next.pay.dto.response.login.LoginResponse;
import com.well.tech.next.pay.dto.response.token.RefreshTokenResponse;
import com.well.tech.next.pay.security.JwtAuthenticationFilter;
import com.well.tech.next.pay.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService service;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String basePath() {

        return ApiVersion.API_BASE_PATH
                + "/"
                + ApiVersion.API_VERSION
                + "/auth";
    }

    @Test
    void shouldLoginUser() throws Exception {

        LoginRequest request =
                new LoginRequest(
                        "john@test.com",
                        "password123"
                );

        LoginResponse response =
                new LoginResponse(
                        "access-token",
                        "refresh-token"
                );

        when(service.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post(basePath() + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("access-token"))
                .andExpect(jsonPath("$.refreshToken")
                        .value("refresh-token"));
    }

    @Test
    void shouldRefreshToken() throws Exception {

        RefreshTokenRequest request =
                new RefreshTokenRequest(
                        "old-refresh-token"
                );

        RefreshTokenResponse response =
                new RefreshTokenResponse(
                        "new-access-token"
                );

        when(service.refreshToken(any(RefreshTokenRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post(basePath() + "/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("new-access-token"));
    }

    @Test
    void shouldLogoutUser() throws Exception {

        LogoutRequest request =
                new LogoutRequest(
                        "refresh-token"
                );

        doNothing()
                .when(service)
                .logout("refresh-token");

        mockMvc.perform(post(basePath() + "/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(service)
                .logout("refresh-token");
    }
}