package com.example.backend_ws.listeners;

import com.example.backend_ws.dtos.SyncEvent;
import com.example.backend_ws.config.RabbitMQConfig;
import com.example.backend_ws.websocket.MonitorWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonitorListener {

    private final MonitorWebSocketHandler wsHandler;

    @RabbitListener(queues = RabbitMQConfig.SYNC_QUEUE)
    public void receiveMonitorUpdate(SyncEvent event) {
        switch (event.type()) {
            case RabbitMQConfig.MONITOR_UPDATE -> wsHandler.broadcastUpdate(event);
            case RabbitMQConfig.MONITOR_ALERT -> wsHandler.broadcastAlert(event);
        }
    }
}
