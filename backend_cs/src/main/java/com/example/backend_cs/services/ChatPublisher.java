package com.example.backend_cs.services;

import com.example.backend_cs.config.RabbitMQConfig;
import com.example.backend_cs.dtos.ChatEvent;
import com.example.backend_cs.entities.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatPublisher {

    private final RabbitTemplate rabbitTemplate;

    private void sendEvent(String type, String sender, String receiver, Map<String, Object> data) {
        ChatEvent event = new ChatEvent(
                type,
                sender,
                receiver,
                data
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                type,
                event
        );
    }

    public void userMessageToAdmin(String sender, String receiver, String message) {
        sendEvent(
                RabbitMQConfig.CHAT_TO_ADMIN,
                sender,
                receiver,
                Map.of("message", message,
                       "timestamp", LocalDateTime.now().toString()
                )
        );
    }

    public void botReplyToUser(String receiver, String message) {
        sendEvent(
                RabbitMQConfig.BOT_TO_USER,
                ChatMessage.BOT_NAME,
                receiver,
                Map.of("message", message,
                       "timestamp", LocalDateTime.now().toString()
                )
        );
    }

    public void adminReplyToUser(String sender, String receiver, String message) {
        sendEvent(
                RabbitMQConfig.CHAT_TO_USER,
                sender,
                receiver,
                Map.of("message", message,
                       "timestamp", LocalDateTime.now().toString()
                )
        );
    }
}
