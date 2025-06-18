package com.whatsapp.chatbot.service;

import com.whatsapp.chatbot.client.WhatsAppClient;
import com.whatsapp.chatbot.dto.WhatsAppEvent;
import com.whatsapp.chatbot.entity.ChatMessage;
import com.whatsapp.chatbot.entity.Conversation;
import com.whatsapp.chatbot.entity.Intent;
import com.whatsapp.chatbot.repository.ChatMessageRepository;
import com.whatsapp.chatbot.repository.IntentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final WhatsAppClient whatsAppClient;
    private final IntentRepository intentRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public void handleWebhookEvent(WhatsAppEvent event) {
        log.debug("Processing webhook event: {}", event);
        
        event.getEntry().forEach(entry -> {
            entry.getChanges().forEach(change -> {
                if ("messages".equals(change.getField()) && change.getValue() != null) {
                    handleMessage(change.getValue());
                } else {
                    log.debug("Skipping non-message change: field={}", change.getField());
                }
            });
        });
    }

    private void handleMessage(WhatsAppEvent.Value value) {
        if (value.getMessages() == null || value.getMessages().isEmpty()) {
            log.debug("No messages in value object");
            return;
        }

        value.getMessages().forEach(message -> {
            if (message.getText() == null || message.getText().getBody() == null) {
                log.debug("Skipping message without text body");
                return;
            }

            String userId = message.getFrom();
            String conversationId = UUID.randomUUID().toString();
            String userMessage = message.getText().getBody();

            log.info("Processing message from {}: {}", userId, userMessage);

            // Store inbound message
            ChatMessage inboundMessage = ChatMessage.inbound(conversationId, userId, userMessage);
            chatMessageRepository.save(inboundMessage);

            // Find matching intent
            Optional<Intent> matchingIntent = findMatchingIntent(userMessage);
            
            // Generate and send response
            String response = matchingIntent
                    .map(Intent::getResponse)
                    .orElse("I'm sorry, I don't understand. Could you please rephrase?");

            try {
                whatsAppClient.sendMessage(userId, response);
                
                // Store outbound message
                ChatMessage outboundMessage = ChatMessage.outbound(
                    conversationId,
                    userId,
                    response,
                    matchingIntent.map(i -> i.getId().toString()).orElse(null)
                );
                chatMessageRepository.save(outboundMessage);
                
                log.info("Sent response to {}: {}", userId, response);
            } catch (Exception e) {
                log.error("Failed to send response to user {}: {}", userId, e.getMessage(), e);
            }
        });
    }

    private Optional<Intent> findMatchingIntent(String message) {
        List<Intent> matchingIntents = intentRepository.findMatchingIntents(message.toLowerCase());
        return matchingIntents.stream().findFirst();
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getConversationHistory(String userId, int limit) {
        return chatMessageRepository.findByUserId(userId, limit);
    }

    @Transactional
    public Intent createIntent(Intent intent) {
        if (intentRepository.existsByName(intent.getName())) {
            throw new IllegalArgumentException("Intent with name " + intent.getName() + " already exists");
        }
        return intentRepository.save(intent);
    }

    @Transactional
    public Intent updateIntent(UUID id, Intent intent) {
        Intent existing = intentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intent not found: " + id));
        
        existing.setName(intent.getName());
        existing.setTrigger(intent.getTrigger());
        existing.setResponse(intent.getResponse());
        existing.setActive(intent.isActive());
        
        return intentRepository.save(existing);
    }

    @Transactional
    public void deleteIntent(UUID id) {
        intentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Intent> getAllIntents() {
        return intentRepository.findByActiveTrue();
    }
} 