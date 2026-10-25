package br.com.acad.auth.application.port.out;

public interface TokenProviderPort {
    String generateAccessToken(String subject);
}
