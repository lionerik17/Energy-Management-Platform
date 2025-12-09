package com.example.backend_cs.dtos;

import java.util.Map;

public record ChatEvent(
        String type,
        String sender,
        String receiver,
        Map<String, Object> data
) { }
