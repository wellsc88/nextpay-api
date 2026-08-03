package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.dto.request.role.UpdateRoleRequest;
import com.well.tech.next.pay.dto.request.role.UpdateRoleStatusRequest;
import com.well.tech.next.pay.dto.response.admin.AdminUserResponse;
import com.well.tech.next.pay.entity.Role;
import com.well.tech.next.pay.entity.User;
import com.well.tech.next.pay.repository.RoleRepository;
import com.well.tech.next.pay.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminUserServiceTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private AdminUserService service;

    @BeforeEach
    void setup() {

        userRepository =
                mock(UserRepository.class);

        roleRepository =
                mock(RoleRepository.class);

        service =
                new AdminUserService(
                        userRepository,
                        roleRepository
                );
    }

    private User user() {

        Role role =
                Role.builder()
                        .name("USER")
                        .build();

        return User.builder()
                .id(UUID.randomUUID())
                .name("Wellington")
                .email("user@email.com")
                .role(role)
                .enabled(true)
                .build();
    }

    @Test
    void shouldFindAllUsersSuccessfully() {

        User user = user();

        when(userRepository.findAll())
                .thenReturn(
                        List.of(user)
                );

        List<AdminUserResponse> response =
                service.findAll();

        assertEquals(
                1,
                response.size()
        );

        assertEquals(
                "user@email.com",
                response.getFirst().email()
        );
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {

        when(userRepository.findAll())
                .thenReturn(
                        List.of()
                );

        List<AdminUserResponse> response =
                service.findAll();

        assertTrue(
                response.isEmpty()
        );
    }

    @Test
    void shouldUpdateUserRoleSuccessfully() {

        UUID id = UUID.randomUUID();

        User user = user();

        Role adminRole =
                Role.builder()
                        .name("ADMIN")
                        .build();

        UpdateRoleRequest request =
                new UpdateRoleRequest(
                        "ADMIN"
                );

        when(userRepository.findById(id))
                .thenReturn(
                        Optional.of(user)
                );

        when(roleRepository.findByName("ADMIN"))
                .thenReturn(
                        Optional.of(adminRole)
                );

        service.updateRole(
                id,
                request
        );

        assertEquals(
                adminRole,
                user.getRole()
        );
        verify(userRepository)
                .save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnUpdateRole() {

        UUID id = UUID.randomUUID();

        when(userRepository.findById(id))
                .thenReturn(
                        Optional.empty()
                );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        service.updateRole(
                                id,
                                new UpdateRoleRequest("ADMIN")
                        )
        );

        verify(roleRepository, never())
                .findByName(any());
    }

    @Test
    void shouldThrowExceptionWhenRoleNotFound() {

        UUID id = UUID.randomUUID();
        User user = user();

        when(userRepository.findById(id))
                .thenReturn(
                        Optional.of(user)
                );

        when(roleRepository.findByName("ADMIN"))
                .thenReturn(
                        Optional.empty()
                );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        service.updateRole(
                                id,
                                new UpdateRoleRequest("ADMIN")
                        )
        );

        verify(userRepository, never())
                .save(user);
    }

    @Test
    void shouldUpdateUserStatusSuccessfully() {

        UUID id = UUID.randomUUID();
        User user = user();

        when(userRepository.findById(id))
                .thenReturn(
                        Optional.of(user)
                );

        UpdateRoleStatusRequest request =
                new UpdateRoleStatusRequest(
                        false
                );

        service.updateStatus(
                id,
                request
        );

        assertFalse(
                user.isEnabled()
        );

        verify(userRepository)
                .save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnUpdateStatus() {

        UUID id = UUID.randomUUID();

        when(userRepository.findById(id))
                .thenReturn(
                        Optional.empty()
                );

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        service.updateStatus(
                                id,
                                new UpdateRoleStatusRequest(false)
                        )
        );

        verify(userRepository, never())
                .save(any());
    }
}