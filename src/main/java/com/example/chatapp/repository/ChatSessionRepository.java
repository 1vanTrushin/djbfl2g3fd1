package com.example.chatapp.repository;

import com.example.chatapp.entity.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ChatSessionEntity
 * Provides database operations for chat sessions
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, String> {

    /**
     * Find all sessions for a chat ID
     */
    List<ChatSessionEntity> findByChatIdOrderByUpdatedAtDesc(String chatId);

    /**
     * Check if session exists
     */
    boolean existsBySessionId(String sessionId);
}
