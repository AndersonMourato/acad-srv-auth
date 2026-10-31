package br.com.acad.auth.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estrutura padrão de erro da API")
public record ApiErrorResponse(
        @Schema(description = "Código de status HTTP", example = "401")
        int status,

        @Schema(description = "Nome do erro HTTP", example = "Unauthorized")
        String error,

        @Schema(description = "Mensagem descritiva do erro", example = "Credenciais inválidas")
        String message
) {
}