package br.com.acad.auth.infrastructure.adapter.out.config;

import br.com.acad.auth.application.port.out.CredentialProviderPort;
import br.com.acad.auth.infrastructure.config.AuthProperties;
import org.springframework.stereotype.Component;

@Component
public class YamlCredentialProvider implements CredentialProviderPort {

    private final AuthProperties authProperties;

    public YamlCredentialProvider(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public boolean areCredentialsValid(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        String configuredUsername = authProperties.username();
        String configuredPassword = authProperties.password();

        if (configuredUsername == null || configuredPassword == null) {
            return false;
        }

        boolean usernameMatches = configuredUsername.equals(username);
        boolean passwordMatches = configuredPassword.equals(password);

        return usernameMatches && passwordMatches;
    }
}