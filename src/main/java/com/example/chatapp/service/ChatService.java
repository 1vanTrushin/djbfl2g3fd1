package com.example.chatapp.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final ChatCheckpointService checkpointService;
    private final ChatClient chatClient;

    public ChatService(ChatCheckpointService checkpointService, ChatClient chatClient) {
        this.checkpointService = checkpointService;
        this.chatClient = chatClient;
    }

    public String processMessage(String threadId, String userMessage) {
        // Load existing context
        Map<String, Object> context = checkpointService.loadCheckpoint(threadId);

        // Add new message to context
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> messages = (List<Map<String, Object>>) context.getOrDefault("messages", new ArrayList<>());
        messages.add(Map.of("role", "user", "content", userMessage));

        // Generate AI response using Spring AI ChatClient
        String aiResponse = chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
        messages.add(Map.of("role", "assistant", "content", aiResponse));

        // Update context
        context.put("messages", messages);

        // Save checkpoint
        checkpointService.saveCheckpoint(threadId, context);

        return aiResponse;
    }
}