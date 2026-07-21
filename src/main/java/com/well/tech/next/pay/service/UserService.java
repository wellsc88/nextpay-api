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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse create(UserRequest request) {

        log.info(
                "Creating user. Email={}",
                request.email()
        );

        if (repository.existsByEmail(request.email())) {

            log.warn(
                    "User creation failed. Email already exists. Email={}",
                    request.email()
            );

            throw new EmailAlreadyExistsException(
                    "Email already registered"
            );
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> {

                    log.error(
                            "Default USER role not found"
                    );

                    return new ResourceNotFoundException(
                            "Role USER not found"
                    );
                });

        User user = mapper.toEntity(request);

        user.setRole(role);

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        User saved =
                repository.save(user);

        log.info(
                "User created successfully. UserId={}, Email={}",
                saved.getId(),
                saved.getEmail()
        );

        return mapper.toResponse(saved);
    }
}