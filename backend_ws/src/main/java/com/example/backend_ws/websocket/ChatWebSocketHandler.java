package com.example.backend_ws.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = extractUsername(session);
        sessions.put(userId, session);
        log("WS connected: " + userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = extractUsername(session);
        sessions.remove(userId);
        log("WS disconnected: " + userId);
    }

    public void sendToUser(String receiverId, Object payload) {
        log("Trying to send to: " + receiverId);
        log("Active WS users: " + sessions.keySet());

        WebSocketSession session = sessions.get(receiverId);

        if (session == null) {
            log("No WS session for user: " + receiverId);
            return;
        }

        if (!session.isOpen()) {
            log("WS session CLOSED for user: " + receiverId);
            return;
        }

        try {
            session.sendMessage(
                    new TextMessage(mapper.writeValueAsString(payload))
            );
            log("WS message sent to " + receiverId);
        } catch (Exception e) {
            log("Error sending WS message: " + e);
        }
    }


    private String extractUsername(WebSocketSession session) {
        String query = session.getUri().getQuery();
        return query.split("=")[1];
    }

    private void log(String msg) {
        System.out.println("[WebSocket] " + msg);
    }

}
