package com.example.backend_monitor.repositories;

import com.example.backend_monitor.entities.DeviceProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceProjectionRepository extends JpaRepository<DeviceProjection, Integer> {
    Optional<DeviceProjection> findByDeviceId(Integer deviceId);
}
