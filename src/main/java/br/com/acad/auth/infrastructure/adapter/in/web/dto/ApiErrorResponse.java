package br.com.acad.auth.infrastructure.adapter.in.web.dto;

public record ApiErrorResponse(
        int status,
        String error,
        String message
) {
}