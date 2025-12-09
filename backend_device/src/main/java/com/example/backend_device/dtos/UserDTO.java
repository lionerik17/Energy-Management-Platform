package com.example.backend_device.dtos;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        Integer id,
        @NotBlank String username
) {}

