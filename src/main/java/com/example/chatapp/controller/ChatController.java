package com.example.chatapp.controller;

import com.example.chatapp.model.ChatRequest;
import com.example.chatapp.service.ChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@ConditionalOnBean(name = "chatClient")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Process a chat message with AI and save checkpoint
     * POST /api/chat/process
     * Body: {"chat": {"chatId": "...", "sessionId": "...", "messageId": "..."}, "messages": [...]}
     * NOTE: Requires OpenAI API key to be configured
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, String>> processMessage(@RequestBody ChatRequest request) {
        if (request.getChat() == null || request.getMessages() == null || request.getMessages().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String chatId = request.getChat().getChatId();
        String sessionId = request.getChat().getSessionId();
        String messageId = request.getChat().getMessageId();

        if (chatId == null || sessionId == null || messageId == null) {
            return ResponseEntity.badRequest().build();
        }

        // Get the last user message
        String userMessage = request.getMessages().get(request.getMessages().size() - 1).getContent();

        String aiResponse = chatService.processMessage(chatId, sessionId, messageId, userMessage);
        
        Map<String, String> response = new HashMap<>();
        response.put("response", aiResponse);
        response.put("sessionId", sessionId);
        
        return ResponseEntity.ok(response);
    }
}