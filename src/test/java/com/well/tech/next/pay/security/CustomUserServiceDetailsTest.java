package com.well.tech.next.pay.security;

import com.well.tech.next.pay.common.exceptions.auth.UserNotFoundException;
import com.well.tech.next.pay.entity.Role;
import com.well.tech.next.pay.entity.User;
import com.well.tech.next.pay.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserServiceDetailsTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void shouldLoadUserByUsername() {

        String email = "john@test.com";

        Role role = Role.builder()
                .id(UUID.randomUUID())
                .name("USER")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email(email)
                .password("encoded-password")
                .role(role)
                .build();

        when(repository.findByEmail(email))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                service.loadUserByUsername(email);

        assertThat(userDetails).isInstanceOf(UserDetailsImpl.class);
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo("encoded-password");

        verify(repository).findByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        String email = "john@test.com";

        when(repository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.loadUserByUsername(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");

        verify(repository).findByEmail(email);
    }
}