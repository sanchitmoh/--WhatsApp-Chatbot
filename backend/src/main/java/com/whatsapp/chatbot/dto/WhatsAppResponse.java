package com.whatsapp.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WhatsAppResponse {
    @JsonProperty("messaging_product")
    private String messagingProduct;
    
    private Contact[] contacts;
    private Message[] messages;

    @Data
    public static class Contact {
        private String input;
        private String waId;
    }

    @Data
    public static class Message {
        private String id;
    }
} 