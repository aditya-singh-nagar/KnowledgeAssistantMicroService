package com.example.DocumentService.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ChatHistoryResponseDto {

	private String id;
	
	private String sessionId;
	
	private RegisterResponseDto user;
	
	private String userQuestion;
	
	private String answer;

	private List<SourceReference> sources;
	
	private LocalDateTime timestamp ;
	
}
