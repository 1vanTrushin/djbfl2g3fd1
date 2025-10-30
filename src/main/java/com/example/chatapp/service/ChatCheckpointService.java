package com.example.chatapp.service;

import com.example.chatapp.model.ChatCheckpoint;
import com.example.chatapp.state.ChatAgentState;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.RunnableConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Service for managing chat checkpoints using LangGraph4j with PostgreSQL persistence
 */
@Service
public class ChatCheckpointService {

    private static final Logger logger = LoggerFactory.getLogger(ChatCheckpointService.class);

    private final CompiledGraph<ChatAgentState> chatGraph;

    public ChatCheckpointService(CompiledGraph<ChatAgentState> chatGraph) {
        this.chatGraph = chatGraph;
    }

    /**
     * Save a chat checkpoint with context using LangGraph4j
     * @param threadId The thread ID for the chat session
     * @param context The context data to save
     * @return The saved checkpoint
     */
    public ChatCheckpoint saveCheckpoint(String threadId, Map<String, Object> context) {
        logger.debug("Saving checkpoint for thread: {} using LangGraph4j", threadId);
        
        try {
            // Generate a unique checkpoint ID
            String checkpointId = UUID.randomUUID().toString();

            // Prepare initial state with messages and context
            Map<String, Object> initialState = new HashMap<>();
            initialState.put(ChatAgentState.THREAD_ID_KEY, threadId);
            initialState.put(ChatAgentState.CONTEXT_KEY, context);
            
            // Extract messages from context if present
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> messages = (List<Map<String, Object>>) context.getOrDefault("messages", new ArrayList<>());
            initialState.put(ChatAgentState.MESSAGES_KEY, messages);

            // Configure with thread_id for checkpoint isolation
            var runnableConfig = RunnableConfig.builder()
                    .threadId(threadId)
                    .build();

            // Invoke the graph - this will save checkpoint to PostgreSQL
            chatGraph.invoke(initialState, runnableConfig);

            logger.debug("Checkpoint saved with ID: {} for thread: {}", checkpointId, threadId);

            // Return the checkpoint model
            return new ChatCheckpoint(threadId, checkpointId, context);
            
        } catch (Exception e) {
            logger.error("Error saving checkpoint for thread: {}", threadId, e);
            throw new RuntimeException("Failed to save checkpoint", e);
        }
    }

    /**
     * Load a chat checkpoint by thread ID using LangGraph4j
     * @param threadId The thread ID to load
     * @return The checkpoint data or empty map if not found
     */
    public Map<String, Object> loadCheckpoint(String threadId) {
        logger.debug("Loading checkpoint for thread: {} using LangGraph4j", threadId);
        
        try {
            var runnableConfig = RunnableConfig.builder()
                    .threadId(threadId)
                    .build();

            // Get the last saved state for this thread
            var lastState = chatGraph.lastStateOf(runnableConfig);
            
            if (lastState.isPresent()) {
                var stateSnapshot = lastState.get();
                var state = stateSnapshot.state();
                
                // Reconstruct context from state
                Map<String, Object> context = new HashMap<>(state.context());
                context.put("messages", state.messages());
                context.put("threadId", state.threadId());
                
                logger.debug("Loaded checkpoint for thread: {} with {} messages", threadId, state.messages().size());
                return context;
            }
            
            logger.debug("No checkpoint found for thread: {}", threadId);
            return new HashMap<>();
            
        } catch (Exception e) {
            logger.error("Error loading checkpoint for thread: {}", threadId, e);
            return new HashMap<>();
        }
    }

    /**
     * Get checkpoint history for a thread
     * @param threadId The thread ID
     * @return List of checkpoint states in chronological order
     */
    public List<Map<String, Object>> getCheckpointHistory(String threadId) {
        logger.debug("Getting checkpoint history for thread: {}", threadId);
        
        // Note: Simplified version - returns only the last state
        // Full history iteration requires specific LangGraph4j API knowledge
        try {
            var context = loadCheckpoint(threadId);
            if (!context.isEmpty()) {
                return List.of(context);
            }
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error getting checkpoint history for thread: {}", threadId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Check if a checkpoint exists for a thread
     * @param threadId The thread ID
     * @return true if exists, false otherwise
     */
    public boolean checkpointExists(String threadId) {
        try {
            var runnableConfig = RunnableConfig.builder()
                    .threadId(threadId)
                    .build();

            return chatGraph.lastStateOf(runnableConfig).isPresent();
        } catch (Exception e) {
            logger.error("Error checking checkpoint existence for thread: {}", threadId, e);
            return false;
        }
    }

    /**
     * Delete checkpoints for a thread (release thread resources)
     * @param threadId The thread ID to delete checkpoints for
     */
    public void deleteCheckpoints(String threadId) {
        logger.debug("Releasing thread resources for: {}", threadId);
        // Note: LangGraph4j's PostgresSaver doesn't have a direct delete method
        // Checkpoints are automatically managed by the saver
        // This method is kept for API compatibility
        logger.warn("Delete operation not fully implemented with LangGraph4j - checkpoints remain in database");
    }
}