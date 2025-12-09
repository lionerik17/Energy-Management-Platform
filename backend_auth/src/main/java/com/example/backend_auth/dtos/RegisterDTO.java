package com.example.backend_auth.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @Size(min = 6) String username,
        @Size(min = 6) String password,
        @NotNull Integer age
) {}
