package com.example.chatapp.model;

import java.time.Instant;
import java.util.Map;

public class ChatCheckpoint {
    private String threadId;
    private String checkpointId;
    private Map<String, Object> context;
    private Instant createdAt;

    // Constructors
    public ChatCheckpoint() {}

    public ChatCheckpoint(String threadId, String checkpointId, Map<String, Object> context) {
        this.threadId = threadId;
        this.checkpointId = checkpointId;
        this.context = context;
        this.createdAt = Instant.now();
    }

    // Getters and Setters
    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getCheckpointId() {
        return checkpointId;
    }

    public void setCheckpointId(String checkpointId) {
        this.checkpointId = checkpointId;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}