package com.example.RagService.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ChatHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@Column(nullable = false)
	private String sessionId;


	@Column(nullable = true)
	private String userEmail;
	
	@Column(columnDefinition = "TEXT",nullable = false)
	private String userQuestion;
	
	@Column(columnDefinition = "TEXT",nullable = false)
	private String answer;
	
	@Column(columnDefinition = "TEXT")
	private String sourcesJson;
	
	@Column(nullable = false)
	private LocalDateTime timestamp = LocalDateTime.now();
}
