package com.smartdoc.ai.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private final String apiKey;

    public ApiKeyFilter(@Value("${smartdoc.api-key:}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String provided = request.getHeader("X-API-Key");
        boolean allowed = apiKey.isBlank()
                || "OPTIONS".equalsIgnoreCase(request.getMethod())
                || (provided != null && MessageDigest.isEqual(
                        apiKey.getBytes(StandardCharsets.UTF_8),
                        provided.getBytes(StandardCharsets.UTF_8)));

        if (allowed) {
            chain.doFilter(request, response);
            return;
        }
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Missing or invalid X-API-Key header\"}");
    }
}