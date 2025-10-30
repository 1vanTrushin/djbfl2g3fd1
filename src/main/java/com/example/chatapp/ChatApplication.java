package com.example.chatapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application
 * Chat application with Spring AI and PostgreSQL-backed conversation memory
 * 
 * OpenAI integration is disabled by default.
 * ChatController and ChatService will be available only if you:
 * 1. Remove OpenAI autoconfiguration exclusions
 * 2. Set spring.ai.openai.api-key in application.properties
 */
@SpringBootApplication(exclude = {
    org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration.class,
    org.springframework.ai.autoconfigure.chat.client.ChatClientAutoConfiguration.class
})
public class ChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }
}