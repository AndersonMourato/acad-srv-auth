package br.com.acad.auth.application.port.out;

public interface CredentialProviderPort {
    boolean areCredentialsValid(String username, String password);
}
