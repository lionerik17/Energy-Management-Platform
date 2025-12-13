package com.example.backend_ws.websocket;

import com.example.backend_ws.dtos.SyncEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MonitorWebSocketHandler extends TextWebSocketHandler {

    // deviceId -> set of sessions
    private final Map<String, Set<WebSocketSession>> subscribers = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log("Client connected");
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> msg = mapper.readValue(message.getPayload(), Map.class);

        if (msg.containsKey("subscribe")) {
            String deviceId = String.valueOf(msg.get("subscribe"));
            subscribers.computeIfAbsent(deviceId, k -> ConcurrentHashMap.newKeySet())
                    .add(session);

            log("Client subscribed to device " + deviceId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        subscribers.values().forEach(set -> set.remove(session));
        log("Client disconnected");
    }

    public void broadcastUpdate(SyncEvent event) {
        String deviceId = String.valueOf(event.deviceId());
        Set<WebSocketSession> subs = subscribers.get(deviceId);

        if (subs == null || subs.isEmpty()) return;

        try {
            Object payload = event.data();

            String json = mapper.writeValueAsString(payload);

            for (WebSocketSession s : subs) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(json));
                }
            }
        } catch (Exception e) {
            log("Error sending update: " + e);
        }
    }

    private void log(String msg) {
        System.out.println("[MonitorWS] " + msg);
    }
}
