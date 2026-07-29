package com.well.tech.next.pay.controller;

import com.well.tech.next.pay.dto.request.role.UpdateRoleRequest;
import com.well.tech.next.pay.dto.request.role.UpdateRoleStatusRequest;
import com.well.tech.next.pay.dto.response.admin.AdminUserResponse;
import com.well.tech.next.pay.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.well.tech.next.pay.config.ApiVersion.API_BASE_PATH;
import static com.well.tech.next.pay.config.ApiVersion.API_VERSION;

@RestController
@RequestMapping(API_BASE_PATH + "/" + API_VERSION + "/admin/users")
@RequiredArgsConstructor
@Tag(
        name = "Admin - Users",
        description = "Administrative operations for user management"
)
public class AdminUserController {

    private final AdminUserService service;


    @Operation(
            summary = "List all users",
            description = "Returns all registered users with administrative information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> findAll() {

        return ResponseEntity.ok(
                service.findAll()
        );
    }


    @Operation(
            summary = "Update user role",
            description = "Changes the role assigned to a user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Role updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid role data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> updateRole(
            @Parameter(
                    description = "User UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    in = ParameterIn.PATH
            )
            @PathVariable UUID id,

            @Valid
            @RequestBody UpdateRoleRequest request
    ) {

        service.updateRole(id, request);

        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Update user status",
            description = "Enable or disable a user account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @Parameter(
                    description = "User UUID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    in = ParameterIn.PATH
            )
            @PathVariable UUID id,

            @Valid
            @RequestBody UpdateRoleStatusRequest request
    ) {

        service.updateStatus(id, request);

        return ResponseEntity.noContent().build();
    }
}