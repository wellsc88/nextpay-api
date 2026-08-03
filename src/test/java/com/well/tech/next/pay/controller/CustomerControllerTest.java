package com.well.tech.next.pay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.well.tech.next.pay.common.exceptions.GlobalExceptionHandler;
import com.well.tech.next.pay.config.ApiVersion;
import com.well.tech.next.pay.dto.request.customer.CreateCustomerRequest;
import com.well.tech.next.pay.dto.request.customer.UpdateCustomerRequest;
import com.well.tech.next.pay.dto.response.customer.CustomerResponse;
import com.well.tech.next.pay.security.CustomUserDetailsService;
import com.well.tech.next.pay.security.JwtAuthenticationFilter;
import com.well.tech.next.pay.security.JwtService;
import com.well.tech.next.pay.service.CustomerService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    private String basePath() {

        return ApiVersion.API_BASE_PATH
                + "/"
                + ApiVersion.API_VERSION
                + "/customers";
    }

    @Test
    void shouldCreateCustomerSuccessfully()
            throws Exception {

        CreateCustomerRequest request =
                new CreateCustomerRequest(
                        "John Doe",
                        "john@test.com",
                        "12345678900"
                );

        CustomerResponse response =
                new CustomerResponse(
                        UUID.randomUUID(),
                        "John Doe",
                        "john@test.com",
                        "12345678900",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(customerService.create(any(CreateCustomerRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post(basePath())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name")
                        .value("John Doe"))
                .andExpect(jsonPath("$.email")
                        .value("john@test.com"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateRequestIsInvalid()
            throws Exception {

        CreateCustomerRequest request =
                new CreateCustomerRequest(
                        "",
                        "",
                        ""
                );

        mockMvc.perform(
                        post(basePath())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindAllCustomersSuccessfully()
            throws Exception {

        CustomerResponse response =
                new CustomerResponse(
                        UUID.randomUUID(),
                        "John Doe",
                        "john@test.com",
                        "12345678900",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(customerService.findAll(
                any(),
                any()
        ))
                .thenReturn(
                        new PageImpl<>(
                                List.of(response),
                                PageRequest.of(0,10),
                                1
                        )
                );

        mockMvc.perform(
                        get(basePath())
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name")
                        .value("John Doe"));
    }

    @Test
    void shouldUpdateCustomerSuccessfully()
            throws Exception {

        UUID id = UUID.randomUUID();

        UpdateCustomerRequest request =
                new UpdateCustomerRequest(
                        "Johnny",
                        "johnny@test.com",
                        "99999999999"
                );

        CustomerResponse response =
                new CustomerResponse(
                        id,
                        "Johnny",
                        "johnny@test.com",
                        "99999999999",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(customerService.update(
                eq(id),
                any(UpdateCustomerRequest.class)
        ))
                .thenReturn(response);

        mockMvc.perform(
                        patch(
                                basePath() + "/{id}",
                                id
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Johnny"));
    }

    @Test
    void shouldDeleteCustomerSuccessfully()
            throws Exception {

        UUID id = UUID.randomUUID();

        doNothing()
                .when(customerService)
                .delete(id);

        mockMvc.perform(
                        delete(
                                basePath() + "/{id}",
                                id
                        )
                )
                .andExpect(status().isNoContent());

        verify(customerService)
                .delete(id);
    }
}