package br.com.acad.auth.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais utilizadas para autenticação")
public record AuthRequest(
        @Schema(description = "Nome de usuário", example = "admin")
        @NotBlank(message = "O usuário é obrigatório")
        String username,

        @Schema(description = "Senha do usuário", example = "admin")
        @NotBlank(message = "A senha é obrigatória")
        String password
) {}