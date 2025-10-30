package com.example.chatapp.repository;

import com.example.chatapp.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ChatMessageEntity
 * Provides database operations for chat messages
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, String> {

    /**
     * Find all messages for a session ordered by creation time
     */
    List<ChatMessageEntity> findBySessionIdOrderByMessageOrderAsc(String sessionId);

    /**
     * Find all messages for a chat ID across all sessions
     */
    List<ChatMessageEntity> findByChatIdOrderByCreatedAtDesc(String chatId);

    /**
     * Find all messages for a specific chat and session
     */
    List<ChatMessageEntity> findByChatIdAndSessionIdOrderByMessageOrderAsc(String chatId, String sessionId);

    /**
     * Count messages in a session
     */
    long countBySessionId(String sessionId);

    /**
     * Delete all messages for a session
     */
    void deleteBySessionId(String sessionId);

    /**
     * Get the last message order number for a session
     */
    @Query("SELECT COALESCE(MAX(m.messageOrder), 0) FROM ChatMessageEntity m WHERE m.sessionId = :sessionId")
    Integer getLastMessageOrder(String sessionId);
}
