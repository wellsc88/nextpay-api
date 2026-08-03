package com.well.tech.next.pay.service;

import com.well.tech.next.pay.common.exceptions.auth.EmailAlreadyExistsException;
import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.dto.request.user.UserRequest;
import com.well.tech.next.pay.dto.response.user.UserResponse;
import com.well.tech.next.pay.entity.Role;
import com.well.tech.next.pay.entity.User;
import com.well.tech.next.pay.mapper.UserMapper;
import com.well.tech.next.pay.repository.RoleRepository;
import com.well.tech.next.pay.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateUserSuccessfully() {

        UserRequest request =
                new UserRequest(
                        "Wellington",
                        "well@test.com",
                        "123456"
                );

        Role role = new Role();
        role.setName("USER");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.email());

        UserResponse response =
                mock(UserResponse.class);

        when(repository.existsByEmail(request.email()))
                .thenReturn(false);

        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.of(role));

        when(mapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");

        when(repository.save(user))
                .thenReturn(user);

        when(mapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.create(request);

        assertNotNull(result);

        verify(repository)
                .existsByEmail(request.email());

        verify(roleRepository)
                .findByName("USER");

        verify(passwordEncoder)
                .encode(request.password());

        verify(repository)
                .save(user);

        verify(mapper)
                .toResponse(user);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        UserRequest request =
                new UserRequest(
                        "Wellington",
                        "well@test.com",
                        "123456"
                );

        when(repository.existsByEmail(request.email()))
                .thenReturn(true);

        EmailAlreadyExistsException exception =
                assertThrows(
                        EmailAlreadyExistsException.class,
                        () -> userService.create(request)
                );

        assertEquals(
                "Email already registered",
                exception.getMessage()
        );

        verify(repository)
                .existsByEmail(request.email());

        verifyNoInteractions(
                roleRepository,
                mapper,
                passwordEncoder
        );
    }

    @Test
    void shouldThrowExceptionWhenUserRoleNotFound() {

        UserRequest request =
                new UserRequest(
                        "Wellington",
                        "well@test.com",
                        "123456"
                );

        when(repository.existsByEmail(request.email()))
                .thenReturn(false);

        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.create(request)
                );

        assertEquals(
                "Role USER not found",
                exception.getMessage()
        );

        verify(repository)
                .existsByEmail(request.email());

        verify(roleRepository)
                .findByName("USER");

        verify(repository, never())
                .save(any());
    }
}