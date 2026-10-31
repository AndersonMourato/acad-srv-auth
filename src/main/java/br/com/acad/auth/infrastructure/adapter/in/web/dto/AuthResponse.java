package br.com.acad.auth.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta contendo o token de acesso gerado")
public record AuthResponse(
        @Schema(description = "Token de acesso JWT")
        String accessToken,

        @Schema(description = "Tipo do token", example = "Bearer")
        String tokenType,

        @Schema(description = "Tempo de expiração em segundos", example = "900")
        long expiresIn
) {}
