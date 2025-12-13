package com.example.backend_device.services;

import com.example.backend_device.config.RabbitMQConfig;
import com.example.backend_device.dtos.SyncEvent;
import com.example.backend_device.entities.Device;
import com.example.backend_device.entities.DevicesUsers;
import com.example.backend_device.entities.UserProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SyncPublisher {

    private final RabbitTemplate rabbitTemplate;

    public static final String DEVICE_CREATED = "DEVICE_CREATED";
    public static final String DEVICE_UPDATED = "DEVICE_UPDATED";
    public static final String DEVICE_DELETED = "DEVICE_DELETED";

    public static final String DEVICE_ATTACH = "DEVICE_ATTACH";
    public static final String DEVICE_UNATTACH = "DEVICE_UNATTACH";
    public static final String DEVICE_UNATTACH_ALL = "DEVICE_UNATTACH_ALL";

    private void sendEvent(String type, Integer deviceId, Map<String, Object> data) {
        SyncEvent event = new SyncEvent(
                type,
                null,
                deviceId,
                data
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                type,
                event
        );
    }

    private void sendEvent(String type, Integer userId, Integer deviceId) {
        SyncEvent event = new SyncEvent(
                type,
                userId,
                deviceId,
                null
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                type,
                event
        );
    }

    private Map<String, Object> baseDeviceData(Device device) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", device.getName());
        map.put("serial", device.getSerialNumber());
        map.put("maxConsumption", device.getMaxConsumptionValue());
        return map;
    }

    public void deviceCreated(Device device) {
        sendEvent(DEVICE_CREATED, device.getId(), baseDeviceData(device));
    }

    public void deviceUpdated(Device device) {
        sendEvent(DEVICE_UPDATED, device.getId(), baseDeviceData(device));
    }

    public void deviceDeleted(Device device) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", device.getName());
        sendEvent(DEVICE_DELETED, device.getId(), data);
    }

    public void deviceAttached(DevicesUsers du) {
        sendEvent(DEVICE_ATTACH, du.getIdUser(), du.getIdDevice());
    }

    public void deviceUnattached(Integer userId, Integer deviceId) {
        sendEvent(DEVICE_UNATTACH, userId, deviceId);
    }

    public void deviceUnattachedAll(Integer userId) {
        SyncEvent event = new SyncEvent(
                DEVICE_UNATTACH_ALL,
                userId,
                null,
                null
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                DEVICE_UNATTACH_ALL,
                event
        );
    }
}
