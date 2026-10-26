package br.com.acad.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RsaKeyConfig {

    @Bean
    public RSAPrivateKey rsaPrivateKey(JwtProperties properties)
            throws Exception {

        byte[] keyBytes = Base64.getDecoder()
                .decode(properties.privateKey());

        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(keyBytes);

        return (RSAPrivateKey) KeyFactory
                .getInstance("RSA")
                .generatePrivate(keySpec);
    }

    @Bean
    public RSAPublicKey rsaPublicKey(JwtProperties properties)
            throws Exception {

        byte[] keyBytes = Base64.getDecoder()
                .decode(properties.publicKey());

        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(keyBytes);

        return (RSAPublicKey) KeyFactory
                .getInstance("RSA")
                .generatePublic(keySpec);
    }
}