package com.well.tech.next.pay.security;

import com.well.tech.next.pay.entity.Role;
import com.well.tech.next.pay.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    @Test
    void shouldReturnUserDetailsProperties() {

        Role role = Role.builder()
                .id(UUID.randomUUID())
                .name("ADMIN")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john@test.com")
                .password("encoded-password")
                .enabled(true)
                .role(role)
                .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        assertThat(userDetails.getUser()).isEqualTo(user);

        assertThat(userDetails.getUsername())
                .isEqualTo("john@test.com");

        assertThat(userDetails.getPassword())
                .isEqualTo("encoded-password");

        assertThat(userDetails.isEnabled())
                .isTrue();

        Collection<? extends GrantedAuthority> authorities =
                userDetails.getAuthorities();

        assertThat(authorities)
                .hasSize(1);

        assertThat(authorities.iterator().next().getAuthority())
                .isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldReturnDisabledUser() {

        Role role = Role.builder()
                .id(UUID.randomUUID())
                .name("USER")
                .build();

        User user = User.builder()
                .email("user@test.com")
                .password("password")
                .enabled(false)
                .role(role)
                .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        assertThat(userDetails.isEnabled())
                .isFalse();

        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }
}