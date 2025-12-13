package com.example.backend_cs.services;

import com.example.backend_cs.entities.ChatMessage;
import com.example.backend_cs.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository repo;
    private final RuleEngineService rules;
    private final ChatPublisher publisher;

    public ChatMessage handleUserMessage(String sender, String receiver, String message) {
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        repo.save(chatMessage);

        publisher.userMessageToAdmin(sender, receiver, message);

        return chatMessage;
    }

    public ChatMessage handleAdminReply(String sender, String receiver, String message) {
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        repo.save(chatMessage);

        publisher.adminReplyToUser(sender, receiver, message);

        return chatMessage;
    }

    public ChatMessage handleBotReply(String sender, String receiver, String message) {
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        repo.save(chatMessage);

        String botReply = rules.match(message);
        if (!botReply.equals(RuleEngineService.REDIRECT_TO_AI)) {
            publisher.botReplyToUser(sender, botReply);
            return chatMessage;
        }

        // TODO: Call Gemini API
        log("Could not match any rule. Calling Gemini...");
        return null;
    }

    private void log(String msg) {
        System.out.println("[CS] " + msg);
    }
}

