package com.example.chatapp.model;

import java.time.Instant;
import java.util.List;

public class ChatCheckpoint {
    private String chatId;      // Постоянный ID пользователя
    private String sessionId;   // ID сессии чата
    private String messageId;   // ID последнего сообщения
    private List<ChatMessage> messages;
    private Instant createdAt;

    // Constructors
    public ChatCheckpoint() {}

    public ChatCheckpoint(String chatId, String sessionId, String messageId, List<ChatMessage> messages) {
        this.chatId = chatId;
        this.sessionId = sessionId;
        this.messageId = messageId;
        this.messages = messages;
        this.createdAt = Instant.now();
    }

    // Getters and Setters
    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}