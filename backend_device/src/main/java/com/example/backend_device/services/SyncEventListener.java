package com.example.backend_device.services;

import com.example.backend_device.config.RabbitMQConfig;
import com.example.backend_device.dtos.SyncEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncEventListener {

    private final DeviceSyncService deviceSyncService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void onEvent(SyncEvent event) {

        if (event == null || event.type() == null) {
            System.out.println("[DeviceSync] Received null/invalid event");
            return;
        }

        switch (event.type()) {
            case RabbitMQConfig.USER_CREATED -> deviceSyncService.handleUserCreated(event);
            case RabbitMQConfig.USER_UPDATED -> deviceSyncService.handleUserUpdated(event);
            case RabbitMQConfig.USER_DELETED -> deviceSyncService.handleUserDeleted(event);
            default -> System.out.println("[DeviceSync] Unknown event type: " + event.type());
        }
    }
}

