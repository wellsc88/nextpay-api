package com.well.tech.next.pay.controller;

import com.well.tech.next.pay.dto.request.user.UserRequest;
import com.well.tech.next.pay.dto.response.user.UserResponse;
import com.well.tech.next.pay.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.well.tech.next.pay.config.ApiVersion.API_BASE_PATH;
import static com.well.tech.next.pay.config.ApiVersion.API_VERSION;

@RestController
@RequestMapping(API_BASE_PATH + "/" + API_VERSION + "/users")
@RequiredArgsConstructor

public class UserController {

    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(
            @Valid @RequestBody UserRequest request) {

        return service.create(request);
    }
}