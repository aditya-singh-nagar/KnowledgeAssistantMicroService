package com.example.MicroserviceProjectOne.service;


import com.example.MicroserviceProjectOne.customException.AppException;
import com.example.MicroserviceProjectOne.customException.NotFound;
import com.example.MicroserviceProjectOne.entities.RefreshToken;
import com.example.MicroserviceProjectOne.enums.SystemCode;
import com.example.MicroserviceProjectOne.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class RefreshTokenService {

	public static final long REFRESH_TOKEN_VALIDITY = 7L*24*60*60*1000;

	private final RefreshTokenRepository refreshRepo;



	public RefreshToken createToken(int userId) {


		RefreshToken token = refreshRepo.findByUserId(userId).orElse(new RefreshToken());

		 token.setUserId(userId);
		 token.setToken(UUID.randomUUID().toString());
		 token.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_VALIDITY));
		 token.setRevoked(false);


		return refreshRepo.save(token);

	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if(token.isRevoked() || token.getExpiryDate().isBefore(Instant.now())) {
				refreshRepo.delete(token);
				throw new AppException(SystemCode.AUTH_TOKEN_EXPIRED_OR_REVOKED, "Refresh token was expired or revoked, please sign in again");
		}
		return token;
	}


	public void revokeToken(String token) {
		RefreshToken rToken = refreshRepo.findByToken(token).orElseThrow(()-> new NotFound(SystemCode.AUTH_TOKEN_EXPIRED_OR_REVOKED, "Token not found"));
		rToken.setRevoked(true);
		refreshRepo.save(rToken);
	}

	public void revokeToken(int userId) {
	 refreshRepo.findByUserId(userId).
				ifPresent(r-> {r.setRevoked(true);
				refreshRepo.save(r);});

	}
}
