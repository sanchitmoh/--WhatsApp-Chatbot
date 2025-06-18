package com.whatsapp.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WhatsAppMessage {
    private String messagingProduct;
    private String to;
    private String type;
    private Text text;

    @Data
    @Builder
    public static class Text {
        private String body;
    }

    @Data
    @Builder
    public static class Contact {
        private String name;
        private String phoneNumber;
    }

    @Data
    @Builder
    public static class Location {
        private double latitude;
        private double longitude;
        private String name;
        private String address;
    }
} 