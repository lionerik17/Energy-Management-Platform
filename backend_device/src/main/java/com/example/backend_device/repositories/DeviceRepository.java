package com.example.backend_device.repositories;

import com.example.backend_device.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Integer> {
    @Query("SELECT d.id FROM Device d")
    List<Integer> findAllIds();
}
