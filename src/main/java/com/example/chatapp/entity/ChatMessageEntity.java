package com.example.chatapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * JPA Entity for storing chat messages in PostgreSQL
 * Provides persistent storage for Spring AI chat history
 */
@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_session_created", columnList = "sessionId, createdAt"),
    @Index(name = "idx_chat_session", columnList = "chatId, sessionId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String chatId; // Permanent user identifier

    @Column(nullable = false)
    private String sessionId; // Session identifier for context isolation

    @Column(nullable = false)
    private String messageId; // Individual message identifier

    @Column(nullable = false, length = 20)
    private String role; // USER, SYSTEM, ASSISTANT, FUNCTION

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Integer messageOrder; // Order of messages in session

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
