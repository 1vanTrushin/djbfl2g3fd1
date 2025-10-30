package com.example.chatapp.service;

import com.example.chatapp.model.ChatCheckpoint;
import com.example.chatapp.model.ChatMessage;
import com.example.chatapp.state.ChatAgentState;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.RunnableConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for managing chat checkpoints using LangGraph4j with PostgreSQL persistence
 * Uses sessionId as the main identifier for checkpoint isolation
 */
@Service
public class ChatCheckpointService {

    private static final Logger logger = LoggerFactory.getLogger(ChatCheckpointService.class);

    private final CompiledGraph<ChatAgentState> chatGraph;

    public ChatCheckpointService(CompiledGraph<ChatAgentState> chatGraph) {
        this.chatGraph = chatGraph;
    }

    /**
     * Save a chat checkpoint with messages using LangGraph4j
     * @param chatId The permanent user chat ID
     * @param sessionId The session ID for checkpoint isolation
     * @param messageId The message ID
     * @param messages The messages to save
     * @return The saved checkpoint
     */
    public ChatCheckpoint saveCheckpoint(String chatId, String sessionId, String messageId, List<ChatMessage> messages) {
        logger.debug("Saving checkpoint for chatId: {}, sessionId: {}", chatId, sessionId);
        
        try {
            // Prepare initial state
            Map<String, Object> initialState = new HashMap<>();
            initialState.put(ChatAgentState.THREAD_ID_KEY, sessionId); // Use sessionId as threadId for LangGraph4j
            
            // Convert ChatMessage objects to maps for LangGraph4j
            List<Map<String, Object>> messagesMaps = new ArrayList<>();
            for (ChatMessage msg : messages) {
                Map<String, Object> msgMap = new HashMap<>();
                msgMap.put("role", msg.getRole());
                msgMap.put("content", msg.getContent());
                messagesMaps.add(msgMap);
            }
            initialState.put(ChatAgentState.MESSAGES_KEY, messagesMaps);
            
            // Add metadata
            Map<String, Object> context = new HashMap<>();
            context.put("chatId", chatId);
            context.put("messageId", messageId);
            initialState.put(ChatAgentState.CONTEXT_KEY, context);

            // Configure with sessionId as thread_id for checkpoint isolation
            var runnableConfig = RunnableConfig.builder()
                    .threadId(sessionId)
                    .build();

            // Invoke the graph - this will save checkpoint to PostgreSQL
            chatGraph.invoke(initialState, runnableConfig);

            logger.debug("Checkpoint saved for sessionId: {}", sessionId);

            // Return the checkpoint model
            return new ChatCheckpoint(chatId, sessionId, messageId, messages);
            
        } catch (Exception e) {
            logger.error("Error saving checkpoint for sessionId: {}", sessionId, e);
            throw new RuntimeException("Failed to save checkpoint", e);
        }
    }

    /**
     * Load a chat checkpoint by session ID using LangGraph4j
     * @param sessionId The session ID to load
     * @return The checkpoint data or null if not found
     */
    public ChatCheckpoint loadCheckpoint(String sessionId) {
        logger.debug("Loading checkpoint for sessionId: {}", sessionId);
        
        try {
            var runnableConfig = RunnableConfig.builder()
                    .threadId(sessionId)
                    .build();

            // Get the last saved state for this session
            var lastState = chatGraph.lastStateOf(runnableConfig);
            
            if (lastState.isPresent()) {
                var stateSnapshot = lastState.get();
                var state = stateSnapshot.state();
                
                // Extract messages
                List<ChatMessage> messages = new ArrayList<>();
                for (Map<String, Object> msgMap : state.messages()) {
                    String role = (String) msgMap.get("role");
                    String content = (String) msgMap.get("content");
                    messages.add(new ChatMessage(role, content));
                }
                
                // Extract metadata
                String chatId = (String) state.context().get("chatId");
                String messageId = (String) state.context().get("messageId");
                
                logger.debug("Loaded checkpoint for sessionId: {} with {} messages", sessionId, messages.size());
                return new ChatCheckpoint(chatId, sessionId, messageId, messages);
            }
            
            logger.debug("No checkpoint found for sessionId: {}", sessionId);
            return null;
            
        } catch (Exception e) {
            logger.error("Error loading checkpoint for sessionId: {}", sessionId, e);
            return null;
        }
    }

    /**
     * Get all sessions for a chat ID
     * @param chatId The chat ID
     * @return List of session IDs
     */
    public List<String> getSessionsForChat(String chatId) {
        logger.debug("Getting sessions for chatId: {}", chatId);
        // Note: This requires additional implementation to track chat-to-session mappings
        // For now, returning empty list
        logger.warn("getSessionsForChat not fully implemented - requires separate mapping storage");
        return new ArrayList<>();
    }

    /**
     * Check if a checkpoint exists for a session
     * @param sessionId The session ID
     * @return true if exists, false otherwise
     */
    public boolean checkpointExists(String sessionId) {
        try {
            var runnableConfig = RunnableConfig.builder()
                    .threadId(sessionId)
                    .build();

            return chatGraph.lastStateOf(runnableConfig).isPresent();
        } catch (Exception e) {
            logger.error("Error checking checkpoint existence for sessionId: {}", sessionId, e);
            return false;
        }
    }

    /**
     * Delete checkpoint for a session
     * @param sessionId The session ID to delete checkpoint for
     */
    public void deleteCheckpoint(String sessionId) {
        logger.debug("Releasing session resources for: {}", sessionId);
        // Note: LangGraph4j's PostgresSaver doesn't have a direct delete method
        // Checkpoints are automatically managed by the saver
        logger.warn("Delete operation not fully implemented with LangGraph4j - checkpoints remain in database");
    }

    /**
     * Create a new session ID for context reset
     * @return New UUID session ID
     */
    public String createNewSessionId() {
        String newSessionId = UUID.randomUUID().toString();
        logger.debug("Created new sessionId: {}", newSessionId);
        return newSessionId;
    }
}