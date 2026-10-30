package com.example.AuthService.repository;


import com.example.AuthService.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;



public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token); //here we are finding refresh token to get new access token

	//doubt on this whether to use it or not
	@Modifying
	int deleteByUserId(int userId);
	
	Optional<RefreshToken> findByUserId(int userId);
}
