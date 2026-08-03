package com.well.tech.next.pay.security;

import com.well.tech.next.pay.common.exceptions.resource.ResourceNotFoundException;
import com.well.tech.next.pay.entity.User;
import com.well.tech.next.pay.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticatedUserService authenticatedUserService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnCurrentUser() {

        String email = "john@test.com";

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        email,
                        null
                )
        );

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email(email)
                .password("password")
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        User result = authenticatedUserService.getCurrentUser();

        assertThat(result).isEqualTo(user);

        verify(userRepository).findByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenCurrentUserDoesNotExist() {

        String email = "john@test.com";

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        email,
                        null
                )
        );

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authenticatedUserService.getCurrentUser())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findByEmail(email);
    }
}