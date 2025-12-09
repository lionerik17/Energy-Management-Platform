package com.example.backend_auth.dtos;

import java.util.Map;

public record SyncEvent(
        String type,
        Integer userId,
        Map<String, Object> data
) {}

