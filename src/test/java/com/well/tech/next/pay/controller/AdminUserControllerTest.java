package com.well.tech.next.pay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.well.tech.next.pay.config.ApiVersion;
import com.well.tech.next.pay.dto.request.role.UpdateRoleRequest;
import com.well.tech.next.pay.dto.request.role.UpdateRoleStatusRequest;
import com.well.tech.next.pay.dto.response.admin.AdminUserResponse;
import com.well.tech.next.pay.security.JwtAuthenticationFilter;
import com.well.tech.next.pay.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminUserService service;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String basePath() {

        return ApiVersion.API_BASE_PATH
                + "/"
                + ApiVersion.API_VERSION
                + "/admin/users";
    }

    @Test
    void shouldFindAllUsers() throws Exception {

        AdminUserResponse response =
                new AdminUserResponse(
                        UUID.randomUUID(),
                        "John Doe",
                        "john@test.com",
                        "ADMIN",
                        true
                );

        when(service.findAll())
                .thenReturn(List.of(response));

        mockMvc.perform(get(basePath()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateUserRole() throws Exception {

        UUID userId = UUID.randomUUID();

        UpdateRoleRequest request =
                new UpdateRoleRequest(
                        "ADMIN"
                );

        doNothing()
                .when(service)
                .updateRole(
                        userId,
                        request
                );

        mockMvc.perform(
                        patch(basePath() + "/{id}/role", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isNoContent());

        verify(service)
                .updateRole(
                        userId,
                        request
                );
    }

    @Test
    void shouldUpdateUserStatus() throws Exception {

        UUID userId = UUID.randomUUID();

        UpdateRoleStatusRequest request =
                new UpdateRoleStatusRequest(
                        true
                );

        doNothing()
                .when(service)
                .updateStatus(
                        userId,
                        request
                );

        mockMvc.perform(
                        patch(basePath() + "/{id}/status", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isNoContent());

        verify(service)
                .updateStatus(
                        userId,
                        request
                );
    }
}