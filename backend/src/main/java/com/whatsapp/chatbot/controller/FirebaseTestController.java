package com.whatsapp.chatbot.controller;

import com.whatsapp.chatbot.service.FirebaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/firebase")
@RequiredArgsConstructor
public class FirebaseTestController {

    private final FirebaseService firebaseService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Test if Firebase is initialized
            firebaseService.getFirebaseAuth();
            firebaseService.getFirestore();
            firebaseService.getFirebaseMessaging();
            
            response.put("status", "healthy");
            response.put("message", "Firebase services are initialized and ready");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Firebase health check failed", e);
            response.put("status", "unhealthy");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/test-notification")
    public ResponseEntity<Map<String, Object>> testNotification(
            @RequestParam String token,
            @RequestParam(defaultValue = "Test Title") String title,
            @RequestParam(defaultValue = "Test message from WhatsApp Chatbot") String body) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> data = new HashMap<>();
            data.put("type", "test");
            data.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            String messageId = firebaseService.sendNotification(token, title, body, data);
            
            response.put("success", true);
            response.put("messageId", messageId);
            response.put("message", "Notification sent successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to send test notification", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/test-topic-notification")
    public ResponseEntity<Map<String, Object>> testTopicNotification(
            @RequestParam String topic,
            @RequestParam(defaultValue = "Topic Test") String title,
            @RequestParam(defaultValue = "Test message to topic") String body) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, String> data = new HashMap<>();
            data.put("type", "topic_test");
            data.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            String messageId = firebaseService.sendNotificationToTopic(topic, title, body, data);
            
            response.put("success", true);
            response.put("messageId", messageId);
            response.put("message", "Topic notification sent successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to send topic notification", e);
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
} 