package com.whatsapp.chatbot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Slf4j
public class ApiKeyAuthFilter extends BasicAuthenticationFilter {

    private final String headerName;
    private final String apiKeyValue;

    public ApiKeyAuthFilter(AuthenticationManager authenticationManager, String headerName, String apiKeyValue) {
        super(authenticationManager);
        this.headerName = headerName;
        this.apiKeyValue = apiKeyValue;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
                                        
                                        String uri = request.getRequestURI(); 
                                           
                                        String path = request.getServletPath();
                                        System.out.println("Request URI: " + uri);
                                        System.out.println("Request Path: " + path);


        // Allow unauthenticated access for webhook verification and public test
        if (uri.equals("/whatsapp/webhook") || 
        uri.equals("/whatsapp/webhook/") || 
        uri.startsWith("/whatsapp/webhook/") || 
        uri.equals("/whatsapp/public-test"))
         {
            log.info("Bypassing API key check for path: {}", path);
    
        filterChain.doFilter(request, response);
        return;
    }

        try {
            String apiKey = request.getHeader(headerName);
            System.out.println("Header Name: " + headerName);
            System.out.println("Received API Key: " + apiKey);
            System.out.println("Expected API Key: " + apiKeyValue);


            if (!StringUtils.hasText(apiKey) || !apiKey.equals(apiKeyValue)) {
                throw new AuthenticationException("Invalid API key") {};

            }
            // ✅ Set Authentication so Spring doesn't treat it as anonymous
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken("api-key-user", null, List.of());
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            log.warn("API key authentication failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid API key");
        }
    }

} 