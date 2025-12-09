package com.example.backend_auth.dtos;

import com.example.backend_auth.entitites.Role;

public record AuthResponse(
        Integer id,
        String accessToken,
        String username,
        Role role,
        long expirationMs
) {}
