package com.example.AuthService.controller;

import com.example.AuthService.dtos.*;
import com.example.AuthService.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> addUser (@RequestBody RegisterRequestDto registerRequestDto){

        return authService.addUser(registerRequestDto);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto>  checkUser(@RequestBody LoginDto loginDto){

        return new  ResponseEntity<>(authService.checkUser(loginDto), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto>  getAccessToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto){

        return new  ResponseEntity<>(authService.generateAccessToken(refreshTokenRequestDto), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String>  logout(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto){
        return new ResponseEntity<>(authService.logout(refreshTokenRequestDto), HttpStatus.OK);
    }


}
