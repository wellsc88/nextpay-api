package com.well.tech.next.pay.controller;

import com.well.tech.next.pay.dto.request.role.UpdateRoleRequest;
import com.well.tech.next.pay.dto.request.role.UpdateRoleStatusRequest;
import com.well.tech.next.pay.dto.response.admin.AdminUserResponse;
import com.well.tech.next.pay.service.AdminUserService;
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
public class AdminUserController {

    private final AdminUserService service;

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> findAll() {

        return ResponseEntity.ok(
                service.findAll()
        );
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest request
    ) {

        service.updateRole(id, request);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleStatusRequest request
    ) {

        service.updateStatus(id, request);

        return ResponseEntity.noContent().build();
    }
}