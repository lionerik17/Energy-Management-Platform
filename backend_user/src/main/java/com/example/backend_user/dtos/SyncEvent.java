package com.example.backend_user.dtos;

import java.util.Map;

public record SyncEvent(
        String type,
        Integer userId,
        Map<String, Object> data
) { }
