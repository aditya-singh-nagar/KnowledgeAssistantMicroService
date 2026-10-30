package com.example.MicroserviceProjectOne.service;

import java.time.Instant;
import java.util.List;


import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccessTokenService {

    private final JwtEncoder jwtEncoder;



    public String generateAccessToken(
            String username, String email , String roles ) {

        Instant now = Instant.now();

        JwtClaimsSet claimSet = JwtClaimsSet.builder()
                .issuer("http://auth.example.com")
                .subject(email)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(600))
                .claim("username", username)
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claimSet)
        ).getTokenValue();
    }
}
