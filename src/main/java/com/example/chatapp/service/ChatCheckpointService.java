package com.example.chatapp.service;

import com.example.chatapp.entity.ChatSessionEntity;
import com.example.chatapp.memory.PostgresChatMemory;
import com.example.chatapp.model.ChatCheckpoint;
import com.example.chatapp.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for managing chat checkpoints using Spring AI ChatMemory with PostgreSQL
 * Uses sessionId as the main identifier for checkpoint isolation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatCheckpointService {

    private final PostgresChatMemory chatMemory;

    /**
     * Save a chat checkpoint with messages using Spring AI ChatMemory
     * @param chatId The permanent user chat ID
     * @param sessionId The session ID for checkpoint isolation
     * @param messageId The message ID
     * @param messages The messages to save
     * @return The saved checkpoint
     */
    public ChatCheckpoint saveCheckpoint(String chatId, String sessionId, String messageId, List<ChatMessage> messages) {
        log.debug("Saving checkpoint for chatId: {}, sessionId: {}, messages: {}", chatId, sessionId, messages.size());
        
        try {
            // Convert ChatMessage to Spring AI Message
            List<Message> aiMessages = new ArrayList<>();
            for (ChatMessage msg : messages) {
                Message aiMessage = convertToSpringAiMessage(msg);
                aiMessages.add(aiMessage);
            }

            // Save to database using ChatMemory
            chatMemory.add(chatId, sessionId, messageId, aiMessages);

            log.info("Successfully saved checkpoint for sessionId: {} with {} messages", sessionId, messages.size());

            // Return the checkpoint model
            return new ChatCheckpoint(chatId, sessionId, messageId, messages);
            
        } catch (Exception e) {
            log.error("Error saving checkpoint for sessionId: {}", sessionId, e);
            throw new RuntimeException("Failed to save checkpoint", e);
        }
    }

    /**
     * Load a chat checkpoint by session ID using Spring AI ChatMemory
     * @param sessionId The session ID to load
     * @return The checkpoint data or null if not found
     */
    public ChatCheckpoint loadCheckpoint(String sessionId) {
        log.debug("Loading checkpoint for sessionId: {}", sessionId);
        
        try {
            // Get messages from ChatMemory
            List<Message> aiMessages = chatMemory.get(sessionId, Integer.MAX_VALUE);
            
            if (aiMessages.isEmpty()) {
                log.debug("No checkpoint found for sessionId: {}", sessionId);
                return null;
            }

            // Convert Spring AI Messages to ChatMessage
            List<ChatMessage> messages = new ArrayList<>();
            for (Message aiMsg : aiMessages) {
                ChatMessage msg = convertFromSpringAiMessage(aiMsg);
                messages.add(msg);
            }

            // Get session info to retrieve chatId
            List<ChatSessionEntity> sessions = chatMemory.getSessionsForChat(sessionId);
            String chatId = sessions.isEmpty() ? sessionId : sessions.get(0).getChatId();

            log.info("Loaded checkpoint for sessionId: {} with {} messages", sessionId, messages.size());
            return new ChatCheckpoint(chatId, sessionId, null, messages);
            
        } catch (Exception e) {
            log.error("Error loading checkpoint for sessionId: {}", sessionId, e);
            return null;
        }
    }

    /**
     * Get all sessions for a chat ID
     * @param chatId The chat ID
     * @return List of session entities
     */
    public List<ChatSessionEntity> getSessionsForChat(String chatId) {
        log.debug("Getting sessions for chatId: {}", chatId);
        return chatMemory.getSessionsForChat(chatId);
    }

    /**
     * Check if a checkpoint exists for a session
     * @param sessionId The session ID
     * @return true if exists, false otherwise
     */
    public boolean checkpointExists(String sessionId) {
        try {
            return chatMemory.exists(sessionId);
        } catch (Exception e) {
            log.error("Error checking checkpoint existence for sessionId: {}", sessionId, e);
            return false;
        }
    }

    /**
     * Delete checkpoint for a session
     * @param sessionId The session ID to delete checkpoint for
     */
    public void deleteCheckpoint(String sessionId) {
        log.debug("Deleting checkpoint for sessionId: {}", sessionId);
        try {
            chatMemory.clear(sessionId);
            log.info("Successfully deleted checkpoint for sessionId: {}", sessionId);
        } catch (Exception e) {
            log.error("Error deleting checkpoint for sessionId: {}", sessionId, e);
            throw new RuntimeException("Failed to delete checkpoint", e);
        }
    }

    /**
     * Create a new session ID for context reset
     * @param chatId The chat ID
     * @return New UUID session ID
     */
    public String createNewSessionId(String chatId) {
        String newSessionId = chatMemory.createNewSession(chatId);
        log.info("Created new sessionId: {} for chatId: {}", newSessionId, chatId);
        return newSessionId;
    }

    /**
     * Convert ChatMessage to Spring AI Message
     */
    private Message convertToSpringAiMessage(ChatMessage msg) {
        return switch (msg.getRole().toUpperCase()) {
            case "USER" -> new UserMessage(msg.getContent());
            case "ASSISTANT" -> new AssistantMessage(msg.getContent());
            case "SYSTEM" -> new SystemMessage(msg.getContent());
            default -> new UserMessage(msg.getContent());
        };
    }

    /**
     * Convert Spring AI Message to ChatMessage
     */
    private ChatMessage convertFromSpringAiMessage(Message aiMsg) {
        String role = switch (aiMsg.getMessageType()) {
            case USER -> "USER";
            case ASSISTANT -> "ASSISTANT";
            case SYSTEM -> "SYSTEM";
            default -> "USER";
        };
        return new ChatMessage(role, aiMsg.getContent());
    }
}