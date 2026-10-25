package br.com.acad.auth.application.port.in;

import br.com.acad.auth.domain.model.AuthenticationResult;

public interface AuthenticateUseCase {
    AuthenticationResult authenticate(String username, String password);
}
