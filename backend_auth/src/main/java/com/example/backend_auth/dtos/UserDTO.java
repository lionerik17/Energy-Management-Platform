package com.example.backend_auth.dtos;

import com.example.backend_auth.entitites.Role;
import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        Integer id,
        @NotBlank String username,
        Role role
) {}
