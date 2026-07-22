package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.dto.request.role.UpdateRoleRequest;
import com.well.tech.next.pay.dto.request.role.UpdateRoleStatusRequest;
import com.well.tech.next.pay.dto.response.admin.AdminUserResponse;
import com.well.tech.next.pay.entity.Role;
import com.well.tech.next.pay.entity.User;
import com.well.tech.next.pay.repository.RoleRepository;
import com.well.tech.next.pay.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public List<AdminUserResponse> findAll() {

        log.info("Fetching all users for administrative management");

        List<AdminUserResponse> users =
                userRepository.findAll()
                        .stream()
                        .map(user -> new AdminUserResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole().getName(),
                                user.isEnabled()
                        ))
                        .toList();

        log.info(
                "Users retrieved successfully. Count={}",
                users.size()
        );

        return users;
    }

    @Transactional
    public void updateRole(
            UUID userId,
            UpdateRoleRequest request
    ) {

        log.info(
                "Updating user role. UserId={}, NewRole={}",
                userId,
                request.role()
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {

                    log.warn(
                            "User not found while updating role. UserId={}",
                            userId
                    );

                    return new ResourceNotFoundException(
                            "User not found"
                    );
                });

        Role role = roleRepository.findByName(request.role())
                .orElseThrow(() -> {

                    log.warn(
                            "Role not found. Role={}",
                            request.role()
                    );

                    return new ResourceNotFoundException(
                            "Role not found"
                    );
                });

        user.setRole(role);

        userRepository.save(user);

        log.info(
                "User role updated successfully. UserId={}, Role={}",
                userId,
                role.getName()
        );
    }

    @Transactional
    public void updateStatus(
            UUID userId,
            UpdateRoleStatusRequest request
    ) {

        log.info(
                "Updating user status. UserId={}, Enabled={}",
                userId,
                request.enabled()
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {

                    log.warn(
                            "User not found while updating status. UserId={}",
                            userId
                    );

                    return new ResourceNotFoundException(
                            "User not found"
                    );
                });

        user.setEnabled(request.enabled());

        userRepository.save(user);

        log.info(
                "User status updated successfully. UserId={}, Enabled={}",
                userId,
                request.enabled()
        );
    }
}