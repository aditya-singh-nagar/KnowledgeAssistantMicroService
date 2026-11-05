package com.example.DocumentService.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatSession {

	private String sesssionId;
	
	// this will be useful for the title like in grok or chatgpt
	private String firstQuestion;
	
	private String lastQuestion;
	
	private LocalDateTime lastActive;
	
	private int messageCount;
}
