package com.whatsapp.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaAuditing
@EnableTransactionManagement
@EnableAsync
public class WhatsAppChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhatsAppChatbotApplication.class, args);
    }
} 