package com.example.AuthService.service;

import com.example.AuthService.configuration.UserServiceClient;
import com.example.AuthService.customException.AppException;
import com.example.AuthService.dtos.*;
import com.example.AuthService.entities.RefreshToken;
import com.example.AuthService.enums.SystemCode;
import com.example.AuthService.repository.RefreshTokenRepository;
import com.example.AuthService.utilities.JWTUtility;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshRepo;
    private final UserServiceClient userServiceClient;
    private final JWTUtility  jwtUtility;
    private AccessTokenService accessTokenService;


    @Transactional
    public ResponseEntity<RegisterResponseDto> addUser (RegisterRequestDto registerRequestDto){

       return  userServiceClient.addUser(registerRequestDto);

    }

    @Transactional
    public AuthResponseDto  checkUser  (LoginDto loginDto){

        ResponseEntity<RegisterResponseDto> entity = userServiceClient.checkUser(loginDto);

            RegisterResponseDto response  = entity.getBody();
            RefreshToken refreshToken = refreshTokenService.createToken(response.getId());
            String token = refreshToken.getToken();
            String role = response.getRoles().stream().findFirst().get().substring(5);
            String accessToken = accessTokenService.generateAccessToken(response.getUsername(), response.getEmail(), role);

            return  new AuthResponseDto(accessToken,token) ;




    }

    public AuthResponseDto  generateAccessToken (RefreshTokenRequestDto refreshTokenRequestDto) {

        RefreshToken refreshToken = refreshRepo.findByToken(refreshTokenRequestDto.getRefreshToken()).orElseThrow(() -> new AppException(SystemCode.SYS_BAD_REQUEST, "RefreshToken not found."));
        refreshTokenService.verifyExpiration(refreshToken);
        ResponseEntity<RegisterResponseDto> entity = userServiceClient.getUser(refreshToken.getUserId());

        RegisterResponseDto response = entity.getBody();

            String token = refreshToken.getToken();
            String role = response.getRoles().stream().findFirst().get().substring(5);
            String accessToken = accessTokenService.generateAccessToken(response.getUsername(), response.getEmail(), role);

            return new AuthResponseDto(accessToken, token);


    }


    public String logout (RefreshTokenRequestDto refreshTokenRequestDto) {

        refreshTokenService.revokeToken(refreshTokenRequestDto.getRefreshToken());

        return "Logout successful.";
    }
}
