package com.whatsapp.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppMessageRequest {
    private String messagingProduct;
    private String to;
    private String type;
    private Map<String, Object> text;
    private Map<String, Object> template;
    private Map<String, Object> image;
    private Map<String, Object> audio;
    private Map<String, Object> document;
    private Map<String, Object> video;
    private Map<String, Object> sticker;
    private Map<String, Object> location;
    private Map<String, Object> contacts;
    private Map<String, Object> interactive;
    private Map<String, Object> reaction;
} 