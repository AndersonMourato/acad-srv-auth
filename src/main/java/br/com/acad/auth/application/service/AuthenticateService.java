package br.com.acad.auth.application.service;

import br.com.acad.auth.application.port.in.AuthenticateUseCase;
import br.com.acad.auth.application.port.out.CredentialProviderPort;
import br.com.acad.auth.application.port.out.TokenProviderPort;
import br.com.acad.auth.domain.exception.InvalidCredentialsException;
import br.com.acad.auth.domain.model.AuthenticationResult;
import br.com.acad.auth.infrastructure.config.JwtProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateService implements AuthenticateUseCase {

    private final CredentialProviderPort credentialProvider;
    private final TokenProviderPort tokenProvider;
    private final JwtProperties jwtProperties;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticateService.class);

    public AuthenticateService(
            CredentialProviderPort credentialProvider,
            TokenProviderPort tokenProvider,
            JwtProperties jwtProperties
    ) {
        this.credentialProvider = credentialProvider;
        this.tokenProvider = tokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public AuthenticationResult authenticate(
            String username,
            String password
    ) {
        logger.warn("Validando credenciais...");
        boolean validCredentials = credentialProvider.areCredentialsValid(username, password);

        if (!validCredentials) {
            logger.warn("Falha na autenticação: credenciais inválidas.");
            throw new InvalidCredentialsException();
        }

        logger.info("Credenciais validadas com sucesso. Gerando token de acesso...");
        String accessToken = tokenProvider.generateAccessToken(username);

        logger.info("Token de acesso gerado com sucesso.");
        return new AuthenticationResult(
                accessToken,
                "Bearer",
                jwtProperties.expiration()
        );
    }
}