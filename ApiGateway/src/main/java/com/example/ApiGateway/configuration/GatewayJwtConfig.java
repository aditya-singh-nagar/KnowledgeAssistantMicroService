package com.example.ApiGateway.configuration;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;

@Configuration
public class GatewayJwtConfig {

    @Bean
    SecretKey jwtSecretKey(
            @Value("${secret}") String secret) {

        byte[] decoded = Base64.getDecoder().decode(secret);

        if (decoded.length < 32) {
            throw new IllegalStateException(
                    "JWT HMAC secret must be at least 32 bytes");
        }

        return new SecretKeySpec(decoded, "HmacSHA256");
    }

    @Bean
    ReactiveJwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        return NimbusReactiveJwtDecoder
                .withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
