package com.example.backend_monitor.dtos;

public record AlertPayload(
        String type,
        Double maxAllowed
) {}

