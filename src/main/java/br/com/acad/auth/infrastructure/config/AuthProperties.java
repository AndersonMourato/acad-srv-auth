package br.com.acad.auth.infrastructure.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.credentials")
public record AuthProperties(
        String username,
        String password
) {
}
