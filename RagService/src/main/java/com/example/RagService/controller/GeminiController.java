package com.example.RagService.controller;



import com.example.RagService.dtos.RagResponseGemini;
import com.example.RagService.dtos.SearchRequest;
import com.example.RagService.dtos.VectorStoreRequestDto;
import com.example.RagService.service.RagService;
import com.example.RagService.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class GeminiController {

    private final RagService ragService;
    private final ChatModel chatModel;
    private final VectorStoreService vectorStoreService;

    @PostMapping ("/gemini")
    public String testGemini(@RequestBody SearchRequest searchRequest) {

        return chatModel.call(searchRequest.getQuestion());
    }

    @PostMapping("/ask")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','MANAGER','PUBLIC')")
    public RagResponseGemini askGemini(@RequestBody SearchRequest searchRequest) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String role = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("Application role not found"));

        log.info("Role: {}", role);

        return ragService.ask(searchRequest.getQuestion(),role) ;

    }

    @PostMapping("/chunks")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','MANAGER','PUBLIC')")
    public ResponseEntity<String> StoreDocumentChunks(@RequestBody VectorStoreRequestDto vectorStoreRequestDto) {

        return  new ResponseEntity<>(vectorStoreService.saveDocumentChunks(vectorStoreRequestDto), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @DeleteMapping("/delete/{documentId}")
    public ResponseEntity<String> deleteDocument(@PathVariable("documentId") String documentId) {
        vectorStoreService.deleteChunksByDocumentId(documentId);
        return new ResponseEntity<>("success",HttpStatus.OK);
    }

}
