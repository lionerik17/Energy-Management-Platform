package com.example.backend_monitor.dtos;

import com.example.backend_monitor.entities.HourlyConsumption;

import java.time.LocalDateTime;

public record HourlyConsumptionResponse(
        Integer id,
        Integer deviceId,
        LocalDateTime hour,
        Double totalConsumption
) {
    public static HourlyConsumptionResponse fromEntity(HourlyConsumption e) {
        return new HourlyConsumptionResponse(
                e.getId(),
                e.getDeviceId(),
                e.getHour(),
                e.getTotalConsumption()
        );
    }
}
