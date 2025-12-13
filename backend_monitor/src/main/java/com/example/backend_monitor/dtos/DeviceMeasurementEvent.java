package com.example.backend_monitor.dtos;

public record DeviceMeasurementEvent(
        String timestamp,
        Integer deviceId,
        Double value,
        AlertPayload alert
) {}

