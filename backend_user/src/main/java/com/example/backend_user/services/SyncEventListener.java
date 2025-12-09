package com.example.backend_user.services;

import com.example.backend_user.config.RabbitMQConfig;
import com.example.backend_user.dtos.SyncEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncEventListener {

    private final UserSyncService syncService;

    @RabbitListener(queues = RabbitMQConfig.USER_SYNC_QUEUE)
    public void handleEvent(SyncEvent event) {

        if (event == null || event.type() == null) {
            System.out.println("[UserSync] Received null/invalid event");
            return;
        }

        switch (event.type()) {
            case RabbitMQConfig.USER_CREATED -> syncService.handleUserCreated(event);
            case RabbitMQConfig.USER_UPDATED -> syncService.handleUserUpdated(event);
            case RabbitMQConfig.USER_DELETED -> syncService.handleUserDeleted(event);
            default -> System.out.println("[UserSync] Unknown event type: " + event.type());
        }
    }
}

