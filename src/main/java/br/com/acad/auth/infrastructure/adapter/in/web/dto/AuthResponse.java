package br.com.acad.auth.infrastructure.adapter.in.web.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
