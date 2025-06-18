package com.whatsapp.chatbot.service;

import com.whatsapp.chatbot.config.WhatsAppConfig;
import com.whatsapp.chatbot.dto.WhatsAppMessageRequest;
import com.whatsapp.chatbot.dto.WhatsAppMessageResponse;
import com.whatsapp.chatbot.dto.WhatsAppWebhookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private final WhatsAppConfig whatsAppConfig;
    private final RestTemplate restTemplate;

    /**
     * Send a text message via WhatsApp
     */
    public WhatsAppMessageResponse sendTextMessage(String to, String message) {
        log.info("Sending WhatsApp message to: {}", to);
        
        WhatsAppMessageRequest request = WhatsAppMessageRequest.builder()
                .messagingProduct("whatsapp")
                .to(to)
                .type("text")
                .text(Map.of("body", message))
                .build();

        return sendMessage(request);
    }

    /**
     * Send a template message
     */
    public WhatsAppMessageResponse sendTemplateMessage(String to, String templateName, String languageCode) {
        log.info("Sending WhatsApp template message to: {}", to);
        
        Map<String, Object> template = new HashMap<>();
        template.put("name", templateName);
        template.put("language", Map.of("code", languageCode));

        WhatsAppMessageRequest request = WhatsAppMessageRequest.builder()
                .messagingProduct("whatsapp")
                .to(to)
                .type("template")
                .template(template)
                .build();

        return sendMessage(request);
    }

    /**
     * Send a message with media
     */
    public WhatsAppMessageResponse sendMediaMessage(String to, String mediaId, String caption) {
        log.info("Sending WhatsApp media message to: {}", to);
        
        Map<String, Object> media = new HashMap<>();
        media.put("id", mediaId);
        if (caption != null) {
            media.put("caption", caption);
        }

        WhatsAppMessageRequest request = WhatsAppMessageRequest.builder()
                .messagingProduct("whatsapp")
                .to(to)
                .type("image")
                .image(media)
                .build();

        return sendMessage(request);
    }

    /**
     * Verify webhook challenge
     */
    public boolean verifyWebhook(String mode, String token, String challenge) {
        log.info("Verifying webhook with mode: {}", mode);
        
        if ("subscribe".equals(mode) && whatsAppConfig.getVerifyToken().equals(token)) {
            log.info("Webhook verified successfully");
            return true;
        }
        
        log.warn("Webhook verification failed");
        return false;
    }

    /**
     * Process incoming webhook
     */
    public void processWebhook(WhatsAppWebhookRequest webhookRequest) {
        log.info("Processing webhook: {}", webhookRequest);
        
        // Process the webhook data
        // This will be implemented based on your business logic
        webhookRequest.getEntry().forEach(entry -> {
            entry.getChanges().forEach(change -> {
                if ("messages".equals(change.getValue().getObject())) {
                    change.getValue().getMessages().forEach(message -> {
                        log.info("Received message from {}: {}", 
                                message.getFrom(), message.getText().getBody());
                        // Handle the message here
                    });
                }
            });
        });
    }

    /**
     * Get phone number information
     */
    public Map<String, Object> getPhoneNumberInfo() {
        log.info("Getting phone number information");
        
        String url = whatsAppConfig.getPhoneNumberUrl();
        HttpHeaders headers = createHeaders();
        
        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        
        return response.getBody();
    }

    /**
     * Send message to WhatsApp API
     */
    private WhatsAppMessageResponse sendMessage(WhatsAppMessageRequest request) {
        String url = whatsAppConfig.getMessagesUrl();
        HttpHeaders headers = createHeaders();
        
        try {
            ResponseEntity<WhatsAppMessageResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(request, headers), WhatsAppMessageResponse.class);
            
            log.info("Message sent successfully: {}", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message", e);
            throw new RuntimeException("Failed to send WhatsApp message", e);
        }
    }

    /**
     * Create headers with authorization
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + whatsAppConfig.getAccessToken());
        headers.set("Content-Type", "application/json");
        return headers;
    }
} 