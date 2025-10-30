package com.example.chatapp.model;

public class ChatInfo {
    private String chatId;    // Постоянный ID пользователя
    private String sessionId; // ID сессии чата
    private String messageId; // ID сообщения

    // Constructors
    public ChatInfo() {}

    public ChatInfo(String chatId, String sessionId, String messageId) {
        this.chatId = chatId;
        this.sessionId = sessionId;
        this.messageId = messageId;
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
}
