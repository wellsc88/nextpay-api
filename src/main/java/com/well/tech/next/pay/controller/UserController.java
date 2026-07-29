package com.well.tech.next.pay.controller;

import com.well.tech.next.pay.dto.request.user.UserRequest;
import com.well.tech.next.pay.dto.response.user.UserResponse;
import com.well.tech.next.pay.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.well.tech.next.pay.config.ApiVersion.API_BASE_PATH;
import static com.well.tech.next.pay.config.ApiVersion.API_VERSION;

@RestController
@RequestMapping(API_BASE_PATH + "/" + API_VERSION + "/users")
@RequiredArgsConstructor
@Tag(
        name = "Users",
        description = "User registration and management endpoints"
)
public class UserController {

    private final UserService service;


    @Operation(
            summary = "Create user",
            description = "Registers a new user account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already exists"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(
            @Valid
            @RequestBody UserRequest request
    ) {

        return service.create(request);
    }
}