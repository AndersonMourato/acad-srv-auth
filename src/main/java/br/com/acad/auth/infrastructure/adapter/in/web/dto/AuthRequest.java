package br.com.acad.auth.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "O usuário é obrigatório")
        String username,

        @NotBlank(message = "A senha é obrigatória")
        String password
) {}