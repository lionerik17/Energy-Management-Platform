package com.example.backend_auth.dtos;

import com.example.backend_auth.entitites.Role;
import jakarta.validation.constraints.NotBlank;

public record AuthDTO(
        @NotBlank String username,
        @NotBlank String password,
        Role role
) { }
