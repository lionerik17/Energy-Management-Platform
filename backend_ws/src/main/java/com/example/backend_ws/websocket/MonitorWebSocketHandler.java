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

    private final Map<String, Set<WebSocketSession>> subscribers = new ConcurrentHashMap<>();
    private final Map<Integer, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log("Client connected");
        Integer userId = extractUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
        }
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
        userSessions.values().remove(session);
        log("Client disconnected");
    }

    public void broadcastUpdate(SyncEvent event) {
        String deviceId = String.valueOf(event.deviceId());
        Set<WebSocketSession> subs = subscribers.get(deviceId);

        if (subs == null || subs.isEmpty()) return;

        try {
            Map<String, Object> payload = event.data();
            payload.put("eventType", "MONITOR_UPDATE");
            payload.put("deviceId", event.deviceId());

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

    public void broadcastAlert(SyncEvent event) {

        Integer userId = event.userId();
        if (userId == null) return;

        WebSocketSession session = userSessions.get(userId);
        if (session == null || !session.isOpen()) return;

        try {
            Map<String, Object> payload = event.data();
            payload.put("eventType", "MONITOR_ALERT");
            payload.put("deviceId", event.deviceId());

            session.sendMessage(
                    new TextMessage(mapper.writeValueAsString(payload))
            );
        } catch (Exception ignored) {}
    }

    private Integer extractUserId(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query == null) return null;

        for (String p : query.split("&")) {
            if (p.startsWith("userId=")) {
                return Integer.valueOf(p.split("=")[1]);
            }
        }
        return null;
    }

    private void log(String msg) {
        System.out.println("[MonitorWS] " + msg);
    }
}
