package com.whatsapp.chatbot.config;

import com.whatsapp.chatbot.client.WhatsAppClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
@ConfigurationProperties(prefix = "whatsapp.api")
public class WhatsAppConfig {
    
    private String version = "v17.0";
    private String baseUrl = "https://graph.facebook.com";
    private String accessToken;
    private String verifyToken;
    private String phoneNumberId;
    private String businessAccountId;
    
    public String getApiUrl() {
        return baseUrl + "/" + version;
    }
    
    public String getPhoneNumberUrl() {
        return getApiUrl() + "/" + phoneNumberId;
    }
    
    public String getBusinessAccountUrl() {
        return getApiUrl() + "/" + businessAccountId;
    }
    
    public String getMessagesUrl() {
        return getPhoneNumberUrl() + "/messages";
    }
    
    public String getWebhookUrl() {
        return getPhoneNumberUrl() + "/subscribed_apps";
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WhatsAppClient whatsAppClient(RestTemplate restTemplate) {
        return new WhatsAppClient(restTemplate, baseUrl, accessToken, version, phoneNumberId);
    }
} 