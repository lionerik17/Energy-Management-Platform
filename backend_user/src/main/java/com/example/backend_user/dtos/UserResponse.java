package com.example.backend_user.dtos;

import com.example.backend_user.entities.Role;
import jakarta.validation.constraints.NotBlank;

public record UserResponse(
        Integer id,
        @NotBlank String username,
        Role role,
        Integer age
) {}
