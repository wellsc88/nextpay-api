package com.well.tech.next.pay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.well.tech.next.pay.config.ApiVersion;
import com.well.tech.next.pay.dto.request.user.UserRequest;
import com.well.tech.next.pay.dto.response.user.UserResponse;
import com.well.tech.next.pay.security.JwtAuthenticationFilter;
import com.well.tech.next.pay.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService service;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String basePath() {

        return ApiVersion.API_BASE_PATH
                + "/"
                + ApiVersion.API_VERSION;
    }

    @Test
    void shouldCreateUser() throws Exception {

        UserRequest request =
                new UserRequest(
                        "John Doe",
                        "john@test.com",
                        "password123"
                );

        UserResponse response =
                new UserResponse(
                        UUID.randomUUID(),
                        "John Doe",
                        "john@test.com"
                );


        when(service.create(any(UserRequest.class)))
                .thenReturn(response);


        mockMvc.perform(post(basePath() + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name")
                        .value("John Doe"))
                .andExpect(jsonPath("$.email")
                        .value("john@test.com"));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidRequest() throws Exception {

        UserRequest request =
                new UserRequest(
                        "",
                        "invalid-email",
                        ""
                );


        mockMvc.perform(post(basePath() + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}