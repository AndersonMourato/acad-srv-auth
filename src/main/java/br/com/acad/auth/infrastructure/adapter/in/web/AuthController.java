package br.com.acad.auth.infrastructure.adapter.in.web;

import br.com.acad.auth.application.port.in.AuthenticateUseCase;
import br.com.acad.auth.domain.model.AuthenticationResult;
import br.com.acad.auth.infrastructure.adapter.in.web.dto.ApiErrorResponse;
import br.com.acad.auth.infrastructure.adapter.in.web.dto.AuthRequest;
import br.com.acad.auth.infrastructure.adapter.in.web.dto.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authenticate")
@Tag(name = "Autenticação", description = "Endpoints para emissão de tokens de acesso")
public class AuthController {

    private final AuthenticateUseCase authenticateUseCase;

    public AuthController(AuthenticateUseCase authenticateUseCase) {
        this.authenticateUseCase = authenticateUseCase;
    }

    @Operation(summary = "Gera um token de acesso JWT a partir das credenciais informadas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token gerado com sucesso",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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