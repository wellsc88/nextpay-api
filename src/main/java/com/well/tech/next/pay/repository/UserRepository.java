package com.well.tech.next.pay.repository;

import com.well.tech.next.pay.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(@NotBlank @Email String email);
    Optional<User> findByEmail(@NotBlank @Email String email);
}