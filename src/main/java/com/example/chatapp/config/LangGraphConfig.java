package com.example.chatapp.config;

import com.example.chatapp.state.ChatAgentState;
import org.bsc.langgraph4j.checkpoint.PostgresSaver;
import org.bsc.langgraph4j.serializer.std.ObjectStreamStateSerializer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangGraphConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    /**
     * PostgresSaver bean for LangGraph4j checkpoint persistence
     * Automatically creates necessary tables in PostgreSQL
     */
    @Bean
    public PostgresSaver postgresSaver() {
        try {
            // Extract host, port, database from JDBC URL
            // Format: jdbc:postgresql://host:port/database
            String cleanUrl = datasourceUrl.replace("jdbc:postgresql://", "");
            String[] parts = cleanUrl.split("/");
            String[] hostPort = parts[0].split(":");
            
            String host = hostPort[0];
            int port = hostPort.length > 1 ? Integer.parseInt(hostPort[1]) : 5432;
            String database = parts.length > 1 ? parts[1] : "chat_db";

            var stateSerializer = new ObjectStreamStateSerializer<>(ChatAgentState::new);

            return PostgresSaver.builder()
                    .host(host)
                    .port(port)
                    .user(username)
                    .password(password)
                    .database(database)
                    .stateSerializer(stateSerializer)
                    .createTables(true) // Automatically create LangGraph4j tables
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create PostgresSaver", e);
        }
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}