package com.example.chatapp.controller;

import com.example.chatapp.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Process a chat message and save checkpoint
     * POST /api/chat/message
     * Body: {"threadId": "thread123", "message": "Hello"}
     */
    @PostMapping("/message")
    public ResponseEntity<String> processMessage(@RequestBody Map<String, String> request) {
        String threadId = request.get("threadId");
        String message = request.get("message");

        if (threadId == null || message == null) {
            return ResponseEntity.badRequest().build();
        }

        String response = chatService.processMessage(threadId, message);
        return ResponseEntity.ok(response);
    }
}