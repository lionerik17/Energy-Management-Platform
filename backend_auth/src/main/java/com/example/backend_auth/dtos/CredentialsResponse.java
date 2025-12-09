package com.example.backend_auth.dtos;

import com.example.backend_auth.entitites.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CredentialsResponse(
        Integer id,
        @Size(min = 6) String username,
        @Size(min = 6) String password,
        @NotNull Role role
) { }
