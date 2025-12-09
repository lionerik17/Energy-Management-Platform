package com.example.backend_monitor.repositories;

import com.example.backend_monitor.entities.HourlyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HourlyConsumptionRepository extends JpaRepository<HourlyConsumption, Long> {
    Optional<HourlyConsumption> findByDeviceIdAndHour(Integer deviceId, LocalDateTime hour);
    List<HourlyConsumption> findAllByDeviceId(Integer deviceId);
    @Query("""
        SELECT h FROM HourlyConsumption h
        WHERE h.deviceId = :deviceId
          AND h.hour >= :start
          AND h.hour < :end
        ORDER BY h.hour ASC
    """)
    List<HourlyConsumption> findForDay(
            Integer deviceId,
            LocalDateTime start,
            LocalDateTime end
    );
    void deleteByDeviceId(Integer deviceId);
}

