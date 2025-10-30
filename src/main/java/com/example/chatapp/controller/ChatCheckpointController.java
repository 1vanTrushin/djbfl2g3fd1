package com.example.chatapp.controller;

import com.example.chatapp.model.ChatCheckpoint;
import com.example.chatapp.model.ChatRequest;
import com.example.chatapp.service.ChatCheckpointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatCheckpointController {

    private final ChatCheckpointService checkpointService;

    public ChatCheckpointController(ChatCheckpointService checkpointService) {
        this.checkpointService = checkpointService;
    }

    /**
     * Save or update a chat checkpoint
     * POST /api/chat/message
     * Body: {"chat": {"chatId": "...", "sessionId": "...", "messageId": "..."}, "messages": [...]}
     */
    @PostMapping("/message")
    public ResponseEntity<ChatCheckpoint> saveMessage(@RequestBody ChatRequest request) {
        if (request.getChat() == null || request.getMessages() == null) {
            return ResponseEntity.badRequest().build();
        }

        String chatId = request.getChat().getChatId();
        String sessionId = request.getChat().getSessionId();
        String messageId = request.getChat().getMessageId();

        if (chatId == null || sessionId == null || messageId == null) {
            return ResponseEntity.badRequest().build();
        }

        ChatCheckpoint saved = checkpointService.saveCheckpoint(chatId, sessionId, messageId, request.getMessages());
        return ResponseEntity.ok(saved);
    }

    /**
     * Load a chat checkpoint by session ID
     * GET /api/chat/session/{sessionId}
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ChatCheckpoint> loadSession(@PathVariable String sessionId) {
        ChatCheckpoint checkpoint = checkpointService.loadCheckpoint(sessionId);
        if (checkpoint == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(checkpoint);
    }

    /**
     * Check if session exists
     * HEAD /api/chat/session/{sessionId}
     */
    @RequestMapping(value = "/session/{sessionId}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> sessionExists(@PathVariable String sessionId) {
        boolean exists = checkpointService.checkpointExists(sessionId);
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * Delete session checkpoint
     * DELETE /api/chat/session/{sessionId}
     */
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable String sessionId) {
        checkpointService.deleteCheckpoint(sessionId);
        return ResponseEntity.ok().build();
    }

    /**
     * Reset context - create a new session ID
     * POST /api/chat/reset-context
     * Body: {"chatId": "..."}
     */
    @PostMapping("/reset-context")
    public ResponseEntity<Map<String, String>> resetContext(@RequestBody Map<String, String> request) {
        String chatId = request.get("chatId");
        if (chatId == null) {
            return ResponseEntity.badRequest().build();
        }

        String newSessionId = checkpointService.createNewSessionId(chatId);
        
        Map<String, String> response = new HashMap<>();
        response.put("chatId", chatId);
        response.put("sessionId", newSessionId);
        
        return ResponseEntity.ok(response);
    }
}