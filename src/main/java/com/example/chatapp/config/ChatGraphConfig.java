package com.example.chatapp.config;

import com.example.chatapp.state.ChatAgentState;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.checkpoint.PostgresSaver;
import org.bsc.langgraph4j.CompileConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * Configuration for LangGraph4j StateGraph
 * Defines the chat workflow with checkpoint persistence
 */
@Configuration
public class ChatGraphConfig {

    /**
     * Compiled graph for chat workflow with PostgreSQL persistence
     * 
     * Graph structure:
     * START -> process_message -> save_context -> END
     */
    @Bean
    public CompiledGraph<ChatAgentState> chatGraph(PostgresSaver postgresSaver) {
        try {
            // Define the state graph
            var stateGraph = new StateGraph<>(ChatAgentState::new);

            // Node: Process incoming message
            stateGraph.addNode("process_message", node_async(state -> {
                // This node can be extended to process messages through LLM
                // For now, it just passes the message through
                return Map.of(
                        ChatAgentState.MESSAGES_KEY, state.messages(),
                        ChatAgentState.THREAD_ID_KEY, state.threadId()
                );
            }));

            // Node: Save context after processing
            stateGraph.addNode("save_context", node_async(state -> {
                // Update context with processed data
                Map<String, Object> updatedContext = Map.of(
                        "lastProcessedAt", System.currentTimeMillis(),
                        "messageCount", state.messages().size()
                );
                
                return Map.of(
                        ChatAgentState.CONTEXT_KEY, updatedContext
                );
            }));

            // Define the flow
            stateGraph.addEdge(START, "process_message");
            stateGraph.addEdge("process_message", "save_context");
            stateGraph.addEdge("save_context", END);

            // Compile with PostgresSaver for checkpoint persistence
            var compileConfig = CompileConfig.builder()
                    .checkpointSaver(postgresSaver)
                    .build();

            return stateGraph.compile(compileConfig);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create chat graph", e);
        }
    }
}
