package com.example.backend_monitor.dtos;

import java.util.Map;

public record SyncEvent(
        String type,
        Integer userId,
        Integer deviceId,
        Map<String, Object> data
) { }
