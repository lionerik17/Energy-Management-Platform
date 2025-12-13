package com.example.backend_cs.services;

import org.springframework.stereotype.Service;

@Service
public class RuleEngineService {

    public static final String REDIRECT_TO_AI = "CALL_AI";

    public String match(String text) {
        if (text == null || text.isBlank()) {
            return "I'm sorry, I didn't understand. Could you rephrase?";
        }

        String t = text.toLowerCase().trim();

        if (t.matches(".*\\b(hi|hello|hey)\\b.*")) {
            return "Hello! How can I help you today?";
        } else if (t.matches(".*\\b(how are you|how's it going)\\b.*")) {
            return "I'm just code, but I'm running perfectly! What can I help you with?";
        } else if (t.contains("high consumption")) {
            return "If you're seeing unusually high consumption, please check your device.";
        } else if (t.contains("forgot password") || t.contains("reset password")) {
            return "You can reset your password by asking an admin.";
        } else if (t.contains("where") && t.contains("find")) {
            return "You can navigate using the sidebar menu. Let me know specifically what you're looking for!";
        } else if (t.contains("bug") || t.contains("not working") || t.contains("error")) {
            return "I'm sorry you're experiencing an issue. Could you describe exactly what happened so we can check it?";
        } else if (t.contains("what can you do") || t.contains("help me") || t.contains("what is this")) {
            return "I can help answer questions, guide you through the platform, or connect you with an admin. What do you need help with?";
        } else if (t.contains("contact admin") || t.contains("admin help") || t.contains("talk to admin")) {
            return "You can select an administrator from the chat selection page to start a conversation.";
        } else if (t.contains("device info") || t.contains("device information") ||
                t.contains("device details") || t.contains("serial number") ||
                t.contains("my device")) {
            return "You can view all device information in the sidebar -> Devices.";
        }

        return REDIRECT_TO_AI;
    }
}
