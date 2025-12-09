package com.example.backend_monitor.services;

import com.example.backend_monitor.dtos.SyncEvent;
import com.example.backend_monitor.entities.DeviceProjection;
import com.example.backend_monitor.repositories.DeviceProjectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MonitorSyncService {

    private final HistoryCleanupService historyCleanupService;
    private final DeviceProjectionRepository deviceProjectionRepository;

    @Transactional
    public void handleDeviceCreated(SyncEvent e) {
        Integer deviceId = e.deviceId();
        if (deviceId == null) {
            log("Missing deviceId");
            return;
        }

        DeviceProjection device = DeviceProjection.builder()
                .deviceId(deviceId)
                .build();

        deviceProjectionRepository.save(device);
        log("Device added in DeviceProjection: " + deviceId);
    }

    @Transactional
    public void handleDeviceUpdated(SyncEvent e) {
        Integer deviceId = e.deviceId();
        if (deviceId == null) {
            log("Missing deviceId");
            return;
        }

        DeviceProjection device = deviceProjectionRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Id not found"));

        deviceProjectionRepository.save(device);
        log("Device updated in DeviceProjection: " + deviceId);
    }

    @Transactional
    public void handleDeviceDeleted(SyncEvent e) {
        Integer deviceId = e.deviceId();
        if (deviceId == null) {
            log("Missing deviceId");
            return;
        }

        DeviceProjection device = deviceProjectionRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Id not found"));

        try {
            historyCleanupService.deleteHistory(deviceId);
            log("History wiped for device " + deviceId);
        } catch (Exception ex) {
            log("Failed cleanup: " + ex.getMessage());
        }

        deviceProjectionRepository.delete(device);
        log("Device removed from DeviceProjection: " + deviceId);
    }

    private void log(String msg) {
        System.out.println("[MonitorSync] " + msg);
    }
}
