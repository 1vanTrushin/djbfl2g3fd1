package com.example.chatapp.controller;

import com.example.chatapp.model.ChatCheckpoint;
import com.example.chatapp.service.ChatCheckpointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat/checkpoints")
public class ChatCheckpointController {

    private final ChatCheckpointService checkpointService;

    public ChatCheckpointController(ChatCheckpointService checkpointService) {
        this.checkpointService = checkpointService;
    }

    /**
     * Save a chat checkpoint
     * POST /api/chat/checkpoints
     * Body: {"threadId": "thread123", "context": {"messages": [...], "state": {...}}}
     */
    @PostMapping
    public ResponseEntity<ChatCheckpoint> saveCheckpoint(@RequestBody Map<String, Object> request) {
        String threadId = (String) request.get("threadId");
        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>) request.get("context");

        if (threadId == null || context == null) {
            return ResponseEntity.badRequest().build();
        }

        ChatCheckpoint saved = checkpointService.saveCheckpoint(threadId, context);
        return ResponseEntity.ok(saved);
    }

    /**
     * Load a chat checkpoint
     * GET /api/chat/checkpoints/{threadId}
     */
    @GetMapping("/{threadId}")
    public ResponseEntity<Map<String, Object>> loadCheckpoint(@PathVariable String threadId) {
        Map<String, Object> context = checkpointService.loadCheckpoint(threadId);
        if (context.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(context);
    }

    /**
     * Get checkpoint history for a thread
     * GET /api/chat/checkpoints/{threadId}/history
     */
    @GetMapping("/{threadId}/history")
    public ResponseEntity<List<Map<String, Object>>> getCheckpointHistory(@PathVariable String threadId) {
        List<Map<String, Object>> history = checkpointService.getCheckpointHistory(threadId);
        if (history.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(history);
    }

    /**
     * Delete all checkpoints for a thread
     * DELETE /api/chat/checkpoints/{threadId}
     */
    @DeleteMapping("/{threadId}")
    public ResponseEntity<Void> deleteCheckpoints(@PathVariable String threadId) {
        checkpointService.deleteCheckpoints(threadId);
        return ResponseEntity.ok().build();
    }

    /**
     * Check if checkpoint exists
     * HEAD /api/chat/checkpoints/{threadId}
     */
    @RequestMapping(value = "/{threadId}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkpointExists(@PathVariable String threadId) {
        boolean exists = checkpointService.checkpointExists(threadId);
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}