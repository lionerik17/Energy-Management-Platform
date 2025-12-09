package com.example.backend_device.services;

import com.example.backend_device.dtos.SyncEvent;
import com.example.backend_device.entities.Device;
import com.example.backend_device.entities.UserProjection;
import com.example.backend_device.repositories.DeviceRepository;
import com.example.backend_device.repositories.DevicesUsersRepository;
import com.example.backend_device.repositories.UserProjectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceSyncService {

    private final DevicesUsersRepository devicesUsersRepository;
    private final UserProjectionRepository userProjectionRepository;

    @Transactional
    public void handleUserCreated(SyncEvent e) {
        userProjectionRepository.save(
                UserProjection.builder()
                        .id(e.userId())
                        .username(e.data().get("username").toString())
                        .build()
        );
        log("User with id " + e.userId() + " created");
    }

    @Transactional
    public void handleUserUpdated(SyncEvent e) {
        UserProjection u = userProjectionRepository.findById(e.userId()).orElse(null);
        if (u == null) return;

        u.setUsername(e.data().get("username").toString());
        userProjectionRepository.save(u);
        log("User with id " + e.userId() + " updated");
    }

    @Transactional
    public void handleUserDeleted(SyncEvent e) {
        devicesUsersRepository.deleteByIdUser(e.userId());
        userProjectionRepository.deleteById(e.userId());
        log("User with id " + e.userId() + " deleted");
    }

    private void log(String msg) {
        System.out.println("[DeviceSync] " + msg);
    }
}

