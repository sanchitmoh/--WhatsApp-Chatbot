package com.whatsapp.chatbot.controller;

import com.whatsapp.chatbot.dto.WhatsAppMessageResponse;
import com.whatsapp.chatbot.dto.WhatsAppWebhookRequest;
import com.whatsapp.chatbot.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/whatsapp")
@RequiredArgsConstructor
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    /**
     * Webhook verification endpoint
     */
    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String token,
            @RequestParam(name = "hub.challenge", required = false) String challenge) {
                if (mode == null || token == null || challenge == null) {
                    return ResponseEntity.ok("WhatsApp Webhook is up! (No query parameters provided)");
                }
        
        log.info("Webhook verification request - mode: {}, token: {}", mode, token);
        
        if (whatsAppService.verifyWebhook(mode, token, challenge)) {
            log.info("Webhook verified successfully");
            return ResponseEntity.ok(challenge);
        } else {
            log.warn("Webhook verification failed");
            return ResponseEntity.status(403).body("Forbidden");
        }
    }

    /**
     * Webhook endpoint for receiving messages
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(@RequestBody WhatsAppWebhookRequest webhookRequest) {
        log.info("Received webhook: {}", webhookRequest);
        
        try {
            whatsAppService.processWebhook(webhookRequest);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.status(500).body("Error");
        }
    }

    /**
     * Send a text message
     */
    @PostMapping("/send/text")
    public ResponseEntity<WhatsAppMessageResponse> sendTextMessage(
            @RequestParam String to,
            @RequestParam String message) {
        
        log.info("Sending text message to: {}", to);
        
        try {
            WhatsAppMessageResponse response = whatsAppService.sendTextMessage(to, message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to send text message", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Send a template message
     */
    @PostMapping("/send/template")
    public ResponseEntity<WhatsAppMessageResponse> sendTemplateMessage(
            @RequestParam String to,
            @RequestParam String templateName,
            @RequestParam(defaultValue = "en_US") String languageCode) {
        
        log.info("Sending template message to: {}", to);
        
        try {
            WhatsAppMessageResponse response = whatsAppService.sendTemplateMessage(to, templateName, languageCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to send template message", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Send a media message
     */
    @PostMapping("/send/media")
    public ResponseEntity<WhatsAppMessageResponse> sendMediaMessage(
            @RequestParam String to,
            @RequestParam String mediaId,
            @RequestParam(required = false) String caption) {
        
        log.info("Sending media message to: {}", to);
        
        try {
            WhatsAppMessageResponse response = whatsAppService.sendMediaMessage(to, mediaId, caption);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to send media message", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Get phone number information
     */
    @GetMapping("/phone-info")
    public ResponseEntity<Map<String, Object>> getPhoneNumberInfo() {
        log.info("Getting phone number information");
        
        try {
            Map<String, Object> info = whatsAppService.getPhoneNumberInfo();
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            log.error("Failed to get phone number info", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = Map.of(
                "status", "healthy",
                "service", "WhatsApp API",
                "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public-test") 
public String publicTest() {
    return "Hello, world!";
}
} 