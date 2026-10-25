package br.com.acad.auth.domain.model;

public record AuthenticationResult(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}