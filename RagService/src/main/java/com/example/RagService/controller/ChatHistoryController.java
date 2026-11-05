package com.example.RagService.controller;


import com.example.RagService.dtos.*;
import com.example.RagService.service.ChatHistoryService;
import com.example.RagService.service.RagService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
@RestController
@RequestMapping("/api/test/chat")
public class ChatHistoryController {

	private final ChatHistoryService chatServ;

	private final RagService ragServ;

	// ask a question with session and if session is not present then a new one is generated

	@PostMapping("/ask")
	@PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','MANAGER','PUBLIC')")
	public ResponseEntity<ChatResponse> askQuestion(@Valid @RequestBody ChatRequest request, Authentication auth){

		String userEmail = auth.getName();
        String role = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("Application role not found"));

		String sessionId = (request.getSessionId() != null && !request.getSessionId().isBlank())
                ? request.getSessionId().trim()
                : UUID.randomUUID().toString();

		//Call RAG engine with question and role-filtered document access
		RagResponseGemini ragResult = ragServ.ask(request.getQuestion(), role);

		List<SourceReference> sources = new ArrayList<>();
		if (ragResult.source() != null) {
            for (SourceResponse s : ragResult.source()) {
                sources.add(new SourceReference(s.getDocumentId(),
                        s.getFileName(),

                        "Chunk " + s.getChunkNumber()
                ));
            }
        }
		chatServ.saveChatHistory(sessionId, userEmail, request.getQuestion(), ragResult.answer(), sources);

		// Return response to user
        return ResponseEntity.ok(new ChatResponse(ragResult.answer(), sessionId, sources));


	}

	//only user and admin should be able to do this

	@GetMapping("history/{sessionId}")
	public ResponseEntity<List<ChatHistoryResponseDto>> getSessionHistory(@PathVariable String sessionId,Authentication auth){
		String userEmail = auth.getName();
		return ResponseEntity.ok(chatServ.getSessionHistory(sessionId, userEmail));
	}

	@GetMapping("/session")
	public ResponseEntity<List<ChatSession>> getUserSession(Authentication auth){
		return ResponseEntity.ok(chatServ.getUserSession(auth.getName()));
	}

	//only user and admin should be able to do this
	@DeleteMapping("/delete/{sessionId}")
	public ResponseEntity<ApiResponse<String>> deleteSession(@PathVariable String sessionId, Authentication auth){

//		String user = auth.getName();
		boolean isAdmin = auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
		chatServ.deleteSession(sessionId, auth.getName(),isAdmin);
		return ResponseEntity.ok(ApiResponse.success("Session deleted successfully", sessionId));
	}




}
