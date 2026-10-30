package com.example.MicroserviceProjectOne.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false,unique = true)
	private String token; //refresh token, used to find the refresh token in db to get the new access token if it is expired
	

	@Column(name = "user_id" , nullable = false)
	private int  userId ;
	
	@Column(name = "expiry_date",nullable = false)
	private Instant expiryDate;
	
	@Column(nullable = false)
	private boolean revoked = false;
	
}
