package com.whatsapp.chatbot.client;

import com.whatsapp.chatbot.dto.WhatsAppMessage;
import com.whatsapp.chatbot.dto.WhatsAppResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class WhatsAppClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String accessToken;
    private final String apiVersion;
    private final String phoneNumberId;

    public WhatsAppResponse sendMessage(String to, String message) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/{version}/{phone-number-id}/messages")
                .buildAndExpand(apiVersion, phoneNumberId)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        WhatsAppMessage request = WhatsAppMessage.builder()
                .messagingProduct("whatsapp")
                .to(to)
                .type("text")
                .text(WhatsAppMessage.Text.builder().body(message).build())
                .build();

        HttpEntity<WhatsAppMessage> entity = new HttpEntity<>(request, headers);
        
        try {
            log.debug("Sending WhatsApp message to {}: {}", to, message);
            return restTemplate.postForObject(url, entity, WhatsAppResponse.class);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message: {}", e.getMessage(), e);
            throw new WhatsAppClientException("Failed to send message", e);
        }
    }

    public static class WhatsAppClientException extends RuntimeException {
        public WhatsAppClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 