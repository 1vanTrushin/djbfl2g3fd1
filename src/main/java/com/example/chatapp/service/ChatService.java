package com.example.chatapp.service;

import com.example.chatapp.model.ChatCheckpoint;
import com.example.chatapp.model.ChatMessage;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnBean(name = "chatClient")
public class ChatService {

    private final ChatCheckpointService checkpointService;
    private final ChatClient chatClient;

    public ChatService(ChatCheckpointService checkpointService, ChatClient chatClient) {
        this.checkpointService = checkpointService;
        this.chatClient = chatClient;
    }

    public String processMessage(String chatId, String sessionId, String messageId, String userMessage) {
        // Load existing context
        ChatCheckpoint checkpoint = checkpointService.loadCheckpoint(sessionId);
        
        List<ChatMessage> messages;
        if (checkpoint != null) {
            messages = new ArrayList<>(checkpoint.getMessages());
        } else {
            messages = new ArrayList<>();
        }

        // Add new user message
        messages.add(new ChatMessage("USER", userMessage));

        // Generate AI response using Spring AI ChatClient
        String aiResponse = chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
        
        // Add assistant response
        messages.add(new ChatMessage("ASSISTANT", aiResponse));

        // Save checkpoint with updated messages
        checkpointService.saveCheckpoint(chatId, sessionId, messageId, messages);

        return aiResponse;
    }
}