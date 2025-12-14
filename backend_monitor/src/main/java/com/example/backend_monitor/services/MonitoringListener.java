package com.example.backend_monitor.services;

import com.example.backend_monitor.config.RabbitMQConfig;
import com.example.backend_monitor.dtos.DeviceMeasurementEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonitoringListener {

    private final MonitoringService monitoringService;

    @RabbitListener(queues = "${MONITOR_QUEUE}", containerFactory = "rabbitListenerContainerFactory")
    public void onMeasurement(DeviceMeasurementEvent event) {
        System.out.println("Received measurement: " + event);
        monitoringService.process(event);
    }
}
