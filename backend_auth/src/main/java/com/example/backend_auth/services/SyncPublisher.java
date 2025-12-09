package com.example.backend_auth.services;

import com.example.backend_auth.config.RabbitMQConfig;
import com.example.backend_auth.dtos.SyncEvent;
import com.example.backend_auth.entitites.Credentials;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SyncPublisher {

    private final RabbitTemplate rabbitTemplate;

    private void sendEvent(String type, Integer id, Map<String, Object> data) {
        SyncEvent event = new SyncEvent(type, id, data);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                type,
                event
        );
    }

    private Map<String, Object> baseCredentialsData(Credentials user) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("role", user.getRole().name());
        return data;
    }

    public void userCreated(Credentials user, Integer age) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("role", user.getRole().name());
        data.put("age", age);
        sendEvent("USER_CREATED", user.getId(), data);
    }

    public void userUpdated(Credentials user) {
        sendEvent("USER_UPDATED", user.getId(), baseCredentialsData(user));
    }

    public void userDeleted(Credentials user) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        sendEvent("USER_DELETED", user.getId(), data);
    }
}

