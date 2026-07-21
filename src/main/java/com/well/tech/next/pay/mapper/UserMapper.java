package com.well.tech.next.pay.mapper;

import com.well.tech.next.pay.dto.request.user.UserRequest;

import com.well.tech.next.pay.dto.response.user.UserResponse;
import com.well.tech.next.pay.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {

        return User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .build();
    }

    public UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}