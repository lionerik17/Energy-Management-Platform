package com.example.backend_cs.controllers;

import com.example.backend_cs.entities.ChatMessage;
import com.example.backend_cs.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send/user")
    public ChatMessage sendUserMessage(
            @RequestParam String sender,
            @RequestParam String receiver,
            @RequestParam String message
    ) {
        return chatService.handleUserMessage(sender, receiver, message);
    }

    @PostMapping("/send/admin")
    public ChatMessage replyAdmin(
            @RequestParam String sender,
            @RequestParam String receiver,
            @RequestParam String message
    ) {
        return chatService.handleAdminReply(sender, receiver, message);
    }

    @PostMapping("/send/bot")
    public ChatMessage replyBot(
            @RequestParam String sender,
            @RequestParam String receiver,
            @RequestParam String message
    ) {
        return chatService.handleBotReply(sender, receiver, message);
    }
}
