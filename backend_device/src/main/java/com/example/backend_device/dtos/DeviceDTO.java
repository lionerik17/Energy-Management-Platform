package com.example.backend_device.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DeviceDTO(
        @Size(min = 6) String serialNumber,
        @Size(min = 6) String name,
        @NotNull Integer maxConsumptionValue
) {}
