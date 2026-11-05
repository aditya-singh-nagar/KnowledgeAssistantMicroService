package com.example.DocumentService.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {

	private String answer;
	
	private String sessionId;
	
	//List of cited sources like document,pages,section
	private List<SourceReference> sources;
}
