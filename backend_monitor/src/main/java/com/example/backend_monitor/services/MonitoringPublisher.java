package com.example.backend_monitor.services;

import com.example.backend_monitor.config.RabbitMQConfig;
import com.example.backend_monitor.dtos.SyncEvent;
import com.example.backend_monitor.entities.HourlyConsumption;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MonitoringPublisher {

    private final RabbitTemplate rabbitTemplate;

    private void send(String type, Integer userId, Integer deviceId, Map<String, Object> data) {
        SyncEvent event = new SyncEvent(
                type,
                userId,
                deviceId,
                data
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SYNC_EXCHANGE,
                type,
                event
        );
    }

    private Map<String, Object> baseHourlyConsumptionData(HourlyConsumption hc) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", null);
        data.put("deviceId", hc.getDeviceId());
        data.put("totalConsumption", hc.getTotalConsumption());
        data.put("hour", hc.getHour().toString());

        return data;
    }

    public void publishUpdate(HourlyConsumption hc) {
        Map<String, Object> data = baseHourlyConsumptionData(hc);
        send("MONITOR_UPDATE", null, hc.getDeviceId(), data);
    }
}
