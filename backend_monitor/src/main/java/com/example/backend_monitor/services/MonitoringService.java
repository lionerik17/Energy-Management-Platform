package com.example.backend_monitor.services;

import com.example.backend_monitor.dtos.DeviceMeasurementEvent;
import com.example.backend_monitor.dtos.HourlyConsumptionResponse;
import com.example.backend_monitor.entities.DeviceProjection;
import com.example.backend_monitor.entities.HourlyConsumption;
import com.example.backend_monitor.repositories.DeviceProjectionRepository;
import com.example.backend_monitor.repositories.HourlyConsumptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final HourlyConsumptionRepository repo;
    private final MonitoringPublisher publisher;
    private final DeviceProjectionRepository deviceProjectionRepository;

    @Transactional
    public void process(DeviceMeasurementEvent e) {

        DeviceProjection device = deviceProjectionRepository.findById(e.deviceId())
                .orElseThrow(() -> new IllegalArgumentException("ID not found"));

        if(device == null) {
            System.out.println("[Monitor] Invalid deviceId: " + e.deviceId());
            return;
        }

        LocalDateTime timestamp = LocalDateTime.parse(e.timestamp());
        LocalDateTime hourKey = timestamp.withSecond(0).withNano(0);

        HourlyConsumption hc = repo.findByDeviceIdAndHour(e.deviceId(), hourKey)
                .orElseGet(() -> HourlyConsumption.builder()
                        .deviceId(e.deviceId())
                        .hour(hourKey)
                        .totalConsumption(0.0)
                        .build()
                );

        hc.setTotalConsumption(e.value());

        publisher.publishLiveUpdate(hc);
        repo.save(hc);
    }

    public List<HourlyConsumptionResponse> getDeviceConsumption(Integer deviceId) {
        return repo.findAllByDeviceId(deviceId).stream()
                .map(HourlyConsumptionResponse::fromEntity)
                .toList();
    }

    public List<HourlyConsumptionResponse> getAllDevicesConsumption() {
        return repo.findAll().stream()
                .map(HourlyConsumptionResponse::fromEntity)
                .toList();
    }

    public List<HourlyConsumptionResponse> getConsumptionForDay(Integer deviceId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return repo.findForDay(deviceId, start, end)
                .stream()
                .map(HourlyConsumptionResponse::fromEntity)
                .toList();
    }
}
