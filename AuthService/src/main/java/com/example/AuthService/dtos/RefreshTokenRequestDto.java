package com.example.AuthService.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDto {

	@NotBlank(message = "refresh token can not be blan")
	private String refreshToken;
}
