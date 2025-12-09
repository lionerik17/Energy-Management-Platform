package com.example.backend_monitor.services;

import com.example.backend_monitor.entities.HourlyConsumption;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonitoringPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishLiveUpdate(HourlyConsumption entry) {
        String destination = "/topic/device/" + entry.getDeviceId();
        messagingTemplate.convertAndSend(destination, entry);
    }
}
