package com.example.chatapp.state;

import org.bsc.langgraph4j.state.AgentState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * State class for chat application using LangGraph4j
 * Stores messages and context for chat sessions
 */
public class ChatAgentState extends AgentState {

    public static final String MESSAGES_KEY = "messages";
    public static final String CONTEXT_KEY = "context";
    public static final String THREAD_ID_KEY = "threadId";

    public ChatAgentState(Map<String, Object> initData) {
        super(initData);
    }

    /**
     * Get all messages in the conversation
     * @return List of message maps containing role and content
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> messages() {
        return this.<List<Map<String, Object>>>value(MESSAGES_KEY)
                .orElse(new ArrayList<>());
    }

    /**
     * Get the conversation context
     * @return Context map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> context() {
        return this.<Map<String, Object>>value(CONTEXT_KEY)
                .orElse(new HashMap<>());
    }

    /**
     * Get the thread ID
     * @return Thread identifier
     */
    public String threadId() {
        return this.<String>value(THREAD_ID_KEY)
                .orElse("");
    }
}
