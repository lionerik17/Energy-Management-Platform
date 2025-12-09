package com.example.backend_ws.listeners;

import com.example.backend_ws.config.RabbitMQConfig;
import com.example.backend_ws.dtos.ChatEvent;
import com.example.backend_ws.websocket.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageListener {

    private final ChatWebSocketHandler chatWebSocketHandler;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CHAT_TO_ADMIN)
    public void onUserMessageToAdmin(ChatEvent event) {
        chatWebSocketHandler.sendToUser(event.receiver(), event);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CHAT_TO_USER)
    public void onAdminMessageToUser(ChatEvent event) {
        chatWebSocketHandler.sendToUser(event.receiver(), event);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_BOT_TO_USER)
    public void onBotMessageToUser(ChatEvent event) {
        chatWebSocketHandler.sendToUser(event.receiver(), event);
    }
}

