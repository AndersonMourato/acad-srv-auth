package br.com.acad.auth.application.service;

import br.com.acad.auth.application.port.in.AuthenticateUseCase;
import br.com.acad.auth.application.port.out.CredentialProviderPort;
import br.com.acad.auth.application.port.out.TokenProviderPort;
import br.com.acad.auth.domain.exception.InvalidCredentialsException;
import br.com.acad.auth.domain.model.AuthenticationResult;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateService implements AuthenticateUseCase {

    private final CredentialProviderPort credentialProvider;
    private final TokenProviderPort tokenProvider;

    public AuthenticateService(
            CredentialProviderPort credentialProvider,
            TokenProviderPort tokenProvider
    ) {
        this.credentialProvider = credentialProvider;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthenticationResult authenticate(
            String username,
            String password
    ) {
        boolean validCredentials =
                credentialProvider.areCredentialsValid(username, password);

        if (!validCredentials) {
            throw new InvalidCredentialsException();
        }

        String accessToken = tokenProvider.generateAccessToken(username);

        return new AuthenticationResult(
                accessToken,
                "Bearer",
                900
        );
    }
}