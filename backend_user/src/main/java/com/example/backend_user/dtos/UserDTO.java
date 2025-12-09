package com.example.backend_user.dtos;

import com.example.backend_user.entities.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDTO(
        Integer id,
        @Size(min = 6) String username,
        Role role,
        @NotNull Integer age
) {}
