package com.well.tech.next.pay.security;

import com.well.tech.next.pay.config.ApiVersion;
import com.well.tech.next.pay.dto.request.login.LoginRequest;
import com.well.tech.next.pay.dto.request.user.UserRequest;
import com.well.tech.next.pay.dto.response.login.LoginResponse;
import com.well.tech.next.pay.dto.response.user.UserResponse;
import com.well.tech.next.pay.service.AuthService;
import com.well.tech.next.pay.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    private String basePath() {

        return ApiVersion.API_BASE_PATH
                + "/"
                + ApiVersion.API_VERSION;
    }


    @BeforeEach
    void setup() {

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(
                        new LoginResponse(
                                "access-token",
                                "refresh-token"
                        )
                );

        when(userService.create(any(UserRequest.class)))
                .thenReturn(
                        new UserResponse(
                                UUID.randomUUID(),
                                "John Doe",
                                "john@test.com"
                        )
                );
    }

    @Test
    void shouldAllowPublicUserEndpoint() throws Exception {

        mockMvc.perform(post(basePath()+ "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "John Doe",
                                "email": "john@test.com",
                                "password": "password123"
                            }
                            """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnForbiddenForUserWithoutAdminRole() throws Exception {

        mockMvc.perform(get(basePath() + "/admin/users")
                        .with(user("user@test.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowPublicAuthEndpoint() throws Exception {

        mockMvc.perform(post(basePath() + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "john@test.com",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldBlockProtectedEndpointWithoutToken() throws Exception {

        mockMvc.perform(get(basePath()+ "/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldBlockAdminEndpointWithoutAuthentication() throws Exception {

        mockMvc.perform(get(basePath()+ "/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateBCryptPasswordEncoder() {

        String encoded =
                passwordEncoder.encode("password123");

        assert passwordEncoder.matches(
                "password123",
                encoded
        );
    }
}