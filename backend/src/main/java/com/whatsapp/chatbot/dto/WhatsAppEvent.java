package com.whatsapp.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class WhatsAppEvent {
    @NotEmpty
    private String object;

    @NotEmpty
    @Valid
    private List<Entry> entry;

    @Data
    public static class Entry {
        private String id;
        
        @NotEmpty
        @Valid
        private List<Change> changes;
    }

    @Data
    public static class Change {
        @Valid
        private Value value;
        
        @NotEmpty
        private String field;
    }

    @Data
    public static class Value {
        @JsonProperty("messaging_product")
        private String messagingProduct;
        
        private Metadata metadata;
        
        @Valid
        private List<Contact> contacts;
        
        @Valid
        private List<Message> messages;
    }

    @Data
    public static class Metadata {
        @JsonProperty("display_phone_number")
        private String displayPhoneNumber;
        
        @JsonProperty("phone_number_id")
        private String phoneNumberId;
    }

    @Data
    public static class Contact {
        private Profile profile;
        @JsonProperty("wa_id")
        private String waId;
    }

    @Data
    public static class Profile {
        private String name;
    }

    @Data
    public static class Message {
        private String from;
        private String id;
        private String timestamp;
        private String type;
        private Text text;
    }

    @Data
    public static class Text {
        private String body;
    }
} 