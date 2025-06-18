package com.whatsapp.chatbot.controller;

import com.whatsapp.chatbot.entity.ChatMessage;
import com.whatsapp.chatbot.entity.Intent;
import com.whatsapp.chatbot.service.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin endpoints for managing the chatbot")
public class AdminController {

    private final ChatbotService chatbotService;

    @GetMapping("/intents")
    @Operation(summary = "List all active intents")
    public ResponseEntity<List<Intent>> listIntents() {
        return ResponseEntity.ok(chatbotService.getAllIntents());
    }

    @PostMapping("/intents")
    @Operation(summary = "Create a new intent")
    public ResponseEntity<Intent> createIntent(@Valid @RequestBody Intent intent) {
        return ResponseEntity.ok(chatbotService.createIntent(intent));
    }

    @PutMapping("/intents/{id}")
    @Operation(summary = "Update an existing intent")
    public ResponseEntity<Intent> updateIntent(
            @Parameter(description = "Intent ID") @PathVariable UUID id,
            @Valid @RequestBody Intent intent) {
        return ResponseEntity.ok(chatbotService.updateIntent(id, intent));
    }

    @DeleteMapping("/intents/{id}")
    @Operation(summary = "Delete an intent")
    public ResponseEntity<Void> deleteIntent(
            @Parameter(description = "Intent ID") @PathVariable UUID id) {
        chatbotService.deleteIntent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/conversations/{userId}")
    @Operation(summary = "Get conversation history for a user")
    public ResponseEntity<List<ChatMessage>> getConversationHistory(
            @Parameter(description = "User's WhatsApp ID") @PathVariable String userId,
            @Parameter(description = "Maximum number of messages to return") 
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(chatbotService.getConversationHistory(userId, limit));
    }
} 