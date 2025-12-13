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

    @RabbitListener(queues = RabbitMQConfig.MONITOR_QUEUE)
    public void receiveMonitorEvent(SyncEvent event) {
        Integer deviceId = event.deviceId();
        if (deviceId != null) {
            wsHandler.broadcastUpdate(event);
        }
    }
}
