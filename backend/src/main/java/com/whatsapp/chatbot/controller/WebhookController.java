package com.whatsapp.chatbot.controller;

import com.whatsapp.chatbot.dto.WhatsAppEvent;
import com.whatsapp.chatbot.service.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/webhook")
@Tag(name = "Webhook", description = "WhatsApp webhook endpoints")
public class WebhookController {

    private final ChatbotService chatbotService;

    @Value("${whatsapp.api.verify-token}")
    private String verifyToken;

    @GetMapping
    @Operation(summary = "Verify webhook URL", description = "Called by WhatsApp to verify the webhook URL")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {
        
        log.info("Received webhook verification request: mode={}, token={}", mode, token);
        
        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            return ResponseEntity.ok(challenge);
        }
        
        return ResponseEntity.badRequest().body("Invalid verification token");
    }

    @PostMapping
    @Operation(summary = "Handle webhook events", description = "Receives and processes WhatsApp messages")
    public ResponseEntity<Map<String, String>> handleWebhook(@Valid @RequestBody WhatsAppEvent event) {
        log.info("Received webhook event: {}", event);
        
        try {
            chatbotService.handleWebhookEvent(event);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            log.error("Error processing webhook event: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
} 