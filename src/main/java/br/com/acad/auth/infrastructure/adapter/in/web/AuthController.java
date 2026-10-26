package br.com.acad.auth.infrastructure.adapter.in.web;

import br.com.acad.auth.application.port.in.AuthenticateUseCase;
import br.com.acad.auth.domain.model.AuthenticationResult;
import br.com.acad.auth.infrastructure.adapter.in.web.dto.AuthRequest;
import br.com.acad.auth.infrastructure.adapter.in.web.dto.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authenticate")
public class AuthController {

    private final AuthenticateUseCase authenticateUseCase;

    public AuthController(AuthenticateUseCase authenticateUseCase) {
        this.authenticateUseCase = authenticateUseCase;
    }

    @PostMapping("/token")
    public ResponseEntity<AuthResponse> generateToken(
            @Valid @RequestBody AuthRequest request) {

        AuthenticationResult result =
                authenticateUseCase.authenticate(
                        request.username(),
                        request.password()
                );

        AuthResponse response = new AuthResponse(
                result.accessToken(),
                result.tokenType(),
                result.expiresIn()
        );

        return ResponseEntity.ok(response);
    }
}