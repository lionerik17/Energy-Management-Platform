package com.example.backend_monitor.services;

import com.example.backend_monitor.dtos.SyncEvent;
import com.example.backend_monitor.entities.DeviceProjection;
import com.example.backend_monitor.entities.DevicesUsersProjection;
import com.example.backend_monitor.entities.UserProjection;
import com.example.backend_monitor.repositories.DeviceProjectionRepository;
import com.example.backend_monitor.repositories.DevicesUsersProjectionRepository;
import com.example.backend_monitor.repositories.UserProjectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MonitorSyncService {

    private final HistoryCleanupService historyCleanupService;
    private final DeviceProjectionRepository deviceProjectionRepository;
    private final UserProjectionRepository userProjectionRepository;
    private final DevicesUsersProjectionRepository devicesUsersProjectionRepository;

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

    @Transactional
    public void handleDeviceAttached(SyncEvent e) {
        if (e.userId() == null || e.deviceId() == null) return;

        devicesUsersProjectionRepository.save(
                DevicesUsersProjection.builder()
                        .userId(e.userId())
                        .deviceId(e.deviceId())
                        .build()
        );

        log("Device " + e.deviceId() + " attached to user " + e.userId());
    }

    @Transactional
    public void handleDeviceUnattached(SyncEvent e) {
        if (e.userId() == null || e.deviceId() == null) return;

        devicesUsersProjectionRepository
                .deleteByUserIdAndDeviceId(e.userId(), e.deviceId());

        log("Device " + e.deviceId() + " unattached from user " + e.userId());
    }

    @Transactional
    public void handleDeviceUnattachedAll(SyncEvent e) {
        if (e.userId() == null) return;

        devicesUsersProjectionRepository.deleteByUserId(e.userId());

        log("All devices unattached from user " + e.userId());
    }

    @Transactional
    public void handleUserCreated(SyncEvent e) {
        if (e.userId() == null) return;

        UserProjection u = new UserProjection(e.userId());
        userProjectionRepository.save(u);
        log("User " + e.userId() + " created");
    }

    @Transactional
    public void handleUserDeleted(SyncEvent e) {
        if (e.userId() == null) return;

        UserProjection u = userProjectionRepository.findById(e.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        devicesUsersProjectionRepository.deleteByUserId(u.getUserId());
        userProjectionRepository.delete(u);
        log("User " + e.userId() + " deleted");
    }

    private void log(String msg) {
        System.out.println("[MonitorSync] " + msg);
    }
}
