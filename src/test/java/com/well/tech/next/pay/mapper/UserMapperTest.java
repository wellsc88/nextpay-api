package com.well.tech.next.pay.mapper;

import com.well.tech.next.pay.dto.request.user.UserRequest;
import com.well.tech.next.pay.dto.response.user.UserResponse;
import com.well.tech.next.pay.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapper();
    }

    @Test
    void shouldConvertUserRequestToEntity() {

        UserRequest request = new UserRequest(
                "Wellington",
                "well@test.com",
                "password123"
        );

        User user = mapper.toEntity(request);

        assertThat(user.getName())
                .isEqualTo("Wellington");

        assertThat(user.getEmail())
                .isEqualTo("well@test.com");

        assertThat(user.getPassword())
                .isEqualTo("password123");

        assertThat(user.getId())
                .isNull();
    }

    @Test
    void shouldConvertUserEntityToResponse() {

        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(id)
                .name("Wellington")
                .email("well@test.com")
                .password("encrypted-password")
                .build();

        UserResponse response = mapper.toResponse(user);

        assertThat(response.id())
                .isEqualTo(id);

        assertThat(response.name())
                .isEqualTo("Wellington");

        assertThat(response.email())
                .isEqualTo("well@test.com");
    }
}