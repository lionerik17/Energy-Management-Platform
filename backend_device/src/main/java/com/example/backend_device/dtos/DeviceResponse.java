package com.example.backend_device.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeviceResponse (
        Integer id,
        @NotBlank String serialNumber,
        @NotBlank String name,
        Integer maxConsumptionValue
) {}
