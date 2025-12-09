package com.example.backend_monitor.services;

import com.example.backend_monitor.repositories.HourlyConsumptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryCleanupService {

    private final HourlyConsumptionRepository hourlyRepo;

    @Transactional
    public void deleteHistory(Integer deviceId) {
        hourlyRepo.deleteByDeviceId(deviceId);
    }
}

