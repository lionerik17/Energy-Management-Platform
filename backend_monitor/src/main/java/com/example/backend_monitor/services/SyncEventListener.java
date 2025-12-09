package com.example.backend_monitor.services;

import com.example.backend_monitor.config.RabbitMQConfig;
import com.example.backend_monitor.dtos.SyncEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncEventListener {

    private final MonitorSyncService monitorSyncService;

    @RabbitListener(queues = RabbitMQConfig.SYNC_QUEUE)
    public void onEvent(SyncEvent event) {

        if (event == null || event.type() == null) {
            System.out.println("[MonitorSync] Received null/invalid event");
            return;
        }

        switch (event.type()) {
            case RabbitMQConfig.DEVICE_CREATED -> monitorSyncService.handleDeviceCreated(event);
            case RabbitMQConfig.DEVICE_UPDATED -> monitorSyncService.handleDeviceUpdated(event);
            case RabbitMQConfig.DEVICE_DELETED -> monitorSyncService.handleDeviceDeleted(event);

            default -> System.out.println("[MonitorSync] Unknown event type: " + event.type());
        }
    }
}
