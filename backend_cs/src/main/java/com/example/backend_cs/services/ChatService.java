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
    private final GeminiService geminiService;

    public ChatMessage handleUserMessage(String sender, String receiver, String message) {
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

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

        publisher.adminReplyToUser(sender, receiver, message);

        return chatMessage;
    }

    public ChatMessage handleBotReply(String receiver, String message) {
        String botReply = rules.match(message);
        String finalMsg = "";

        if (!botReply.equals(RuleEngineService.REDIRECT_TO_AI)) {
            publisher.botReplyToUser(receiver, botReply);
            finalMsg = botReply;
        } else {
            log("Could not match any rule. Calling Gemini...");

            String aiReply = geminiService.askGemini(message);
            publisher.botReplyToUser(receiver, aiReply);
            finalMsg = aiReply;
        }

        log(finalMsg);

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(ChatMessage.BOT_NAME)
                .receiver(receiver)
                .message(finalMsg)
                .timestamp(LocalDateTime.now())
                .build();

        return chatMessage;
    }

    private void log(String msg) {
        System.out.println("[CS] " + msg);
    }
}

