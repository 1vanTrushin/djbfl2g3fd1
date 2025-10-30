package com.example.chatapp.model;

import java.util.List;

public class ChatRequest {
    private ChatInfo chat;
    private List<ChatMessage> messages;

    // Constructors
    public ChatRequest() {}

    public ChatRequest(ChatInfo chat, List<ChatMessage> messages) {
        this.chat = chat;
        this.messages = messages;
    }

    // Getters and Setters
    public ChatInfo getChat() {
        return chat;
    }

    public void setChat(ChatInfo chat) {
        this.chat = chat;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}
