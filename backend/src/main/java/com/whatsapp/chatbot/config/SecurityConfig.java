package com.whatsapp.chatbot.config;

import com.whatsapp.chatbot.security.ApiKeyAuthFilter;
import com.whatsapp.chatbot.security.RateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${security.api-key.header-name}")
    private String apiKeyHeader;

    @Value("${security.rate-limit.enabled}")
    private boolean rateLimitEnabled;

    @Value("${security.rate-limit.capacity}")
    private int rateLimitCapacity;

    @Value("${security.rate-limit.refill-rate}")
    private int rateLimitRefillRate;

    @Value("${security.rate-limit.refill-duration}")
    private int rateLimitRefillDuration;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        String apiKeyValue = "sanchit@123";
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/prometheus").permitAll()
                .requestMatchers("/webhook").permitAll()
                .requestMatchers("/api/whatsapp/webhook", "/api/whatsapp/webhook/", "/api/whatsapp/webhook/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new ApiKeyAuthFilter(authenticationManager, apiKeyHeader, apiKeyValue), 
                           UsernamePasswordAuthenticationFilter.class);

        // Commenting out RateLimitFilter due to constructor mismatch
        //        if (rateLimitEnabled) {
        //            http.addFilterBefore(
        //                new RateLimitFilter(rateLimitCapacity, rateLimitRefillRate, rateLimitRefillDuration),
        //                UsernamePasswordAuthenticationFilter.class
        //            );
        //        }

        return http.build();
    }
} 