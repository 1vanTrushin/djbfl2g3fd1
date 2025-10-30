package com.example.chatapp.memory;

import com.example.chatapp.entity.ChatMessageEntity;
import com.example.chatapp.entity.ChatSessionEntity;
import com.example.chatapp.repository.ChatMessageRepository;
import com.example.chatapp.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PostgreSQL-backed implementation of Spring AI ChatMemory
 * Stores chat history in database with session isolation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostgresChatMemory implements ChatMemory {

    private final ChatMessageRepository messageRepository;
    private final ChatSessionRepository sessionRepository;

    /**
     * Add messages to chat history
     * @param conversationId The session ID
     * @param messages Messages to add
     */
    @Override
    @Transactional
    public void add(String conversationId, List<Message> messages) {
        log.debug("Adding {} messages to session: {}", messages.size(), conversationId);

        // Get or create session
        ChatSessionEntity session = sessionRepository.findById(conversationId)
                .orElseGet(() -> {
                    log.info("Creating new session: {}", conversationId);
                    return ChatSessionEntity.builder()
                            .sessionId(conversationId)
                            .chatId(conversationId) // Default: use sessionId as chatId
                            .messageCount(0)
                            .build();
                });

        // Get next message order
        Integer lastOrder = messageRepository.getLastMessageOrder(conversationId);
        int nextOrder = (lastOrder != null ? lastOrder : 0) + 1;

        // Convert and save messages
        for (Message message : messages) {
            ChatMessageEntity entity = ChatMessageEntity.builder()
                    .messageId(UUID.randomUUID().toString())
                    .chatId(session.getChatId())
                    .sessionId(conversationId)
                    .role(mapMessageTypeToRole(message.getMessageType()))
                    .content(message.getContent())
                    .messageOrder(nextOrder++)
                    .createdAt(Instant.now())
                    .build();

            messageRepository.save(entity);
            log.debug("Saved message: {} with order: {}", entity.getId(), entity.getMessageOrder());
        }

        // Update session
        session.setMessageCount(session.getMessageCount() + messages.size());
        if (!messages.isEmpty()) {
            session.setLastMessagePreview(truncate(messages.get(messages.size() - 1).getContent(), 100));
        }
        sessionRepository.save(session);

        log.info("Successfully added {} messages to session: {}", messages.size(), conversationId);
    }

    /**
     * Add messages with explicit chatId and sessionId
     */
    @Transactional
    public void add(String chatId, String sessionId, String messageId, List<Message> messages) {
        log.debug("Adding {} messages to chatId: {}, sessionId: {}", messages.size(), chatId, sessionId);

        // Get or create session
        ChatSessionEntity session = sessionRepository.findById(sessionId)
                .orElseGet(() -> {
                    log.info("Creating new session: {} for chat: {}", sessionId, chatId);
                    return ChatSessionEntity.builder()
                            .sessionId(sessionId)
                            .chatId(chatId)
                            .messageCount(0)
                            .build();
                });

        // Ensure chatId matches
        if (!session.getChatId().equals(chatId)) {
            log.warn("ChatId mismatch for session: {}. Expected: {}, Got: {}", 
                     sessionId, session.getChatId(), chatId);
            session.setChatId(chatId);
        }

        // Get next message order
        Integer lastOrder = messageRepository.getLastMessageOrder(sessionId);
        int nextOrder = (lastOrder != null ? lastOrder : 0) + 1;

        // Convert and save messages
        for (Message message : messages) {
            ChatMessageEntity entity = ChatMessageEntity.builder()
                    .messageId(messageId != null ? messageId : UUID.randomUUID().toString())
                    .chatId(chatId)
                    .sessionId(sessionId)
                    .role(mapMessageTypeToRole(message.getMessageType()))
                    .content(message.getContent())
                    .messageOrder(nextOrder++)
                    .createdAt(Instant.now())
                    .build();

            messageRepository.save(entity);
            log.debug("Saved message: {} with order: {}", entity.getId(), entity.getMessageOrder());
        }

        // Update session
        session.setMessageCount(session.getMessageCount() + messages.size());
        if (!messages.isEmpty()) {
            session.setLastMessagePreview(truncate(messages.get(messages.size() - 1).getContent(), 100));
        }
        sessionRepository.save(session);

        log.info("Successfully added {} messages to chatId: {}, sessionId: {}", 
                 messages.size(), chatId, sessionId);
    }

    /**
     * Get chat history for a session
     * @param conversationId The session ID
     * @param lastN Number of recent messages to retrieve (ignored, returns all)
     * @return List of messages
     */
    @Override
    @Transactional(readOnly = true)
    public List<Message> get(String conversationId, int lastN) {
        log.debug("Retrieving messages for session: {}", conversationId);

        List<ChatMessageEntity> entities = messageRepository.findBySessionIdOrderByMessageOrderAsc(conversationId);
        List<Message> messages = new ArrayList<>();

        for (ChatMessageEntity entity : entities) {
            Message message = convertToMessage(entity);
            messages.add(message);
        }

        log.info("Retrieved {} messages for session: {}", messages.size(), conversationId);
        return messages;
    }

    /**
     * Clear all messages for a session
     * @param conversationId The session ID
     */
    @Override
    @Transactional
    public void clear(String conversationId) {
        log.debug("Clearing messages for session: {}", conversationId);
        
        messageRepository.deleteBySessionId(conversationId);
        sessionRepository.deleteById(conversationId);
        
        log.info("Cleared all messages for session: {}", conversationId);
    }

    /**
     * Check if session exists
     */
    public boolean exists(String sessionId) {
        return sessionRepository.existsBySessionId(sessionId);
    }

    /**
     * Get all sessions for a chat
     */
    @Transactional(readOnly = true)
    public List<ChatSessionEntity> getSessionsForChat(String chatId) {
        return sessionRepository.findByChatIdOrderByUpdatedAtDesc(chatId);
    }

    /**
     * Create a new session ID
     */
    public String createNewSession(String chatId) {
        String sessionId = UUID.randomUUID().toString();
        log.info("Created new sessionId: {} for chatId: {}", sessionId, chatId);
        return sessionId;
    }

    /**
     * Map Spring AI MessageType to our role string
     */
    private String mapMessageTypeToRole(MessageType messageType) {
        return switch (messageType) {
            case USER -> "USER";
            case ASSISTANT -> "ASSISTANT";
            case SYSTEM -> "SYSTEM";
            default -> "USER";
        };
    }

    /**
     * Convert ChatMessageEntity to Spring AI Message
     */
    private Message convertToMessage(ChatMessageEntity entity) {
        return switch (entity.getRole()) {
            case "USER" -> new UserMessage(entity.getContent());
            case "ASSISTANT" -> new AssistantMessage(entity.getContent());
            case "SYSTEM" -> new org.springframework.ai.chat.messages.SystemMessage(entity.getContent());
            default -> new UserMessage(entity.getContent());
        };
    }

    /**
     * Truncate string to specified length
     */
    private String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
}
