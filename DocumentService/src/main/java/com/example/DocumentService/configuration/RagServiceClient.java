package com.example.DocumentService.configuration;

import com.example.DocumentService.dtos.VectorStoreRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "RagService"
//        , url = "http://localhost:8085"
        ,configuration = FeignClientConfig.class
)
public interface RagServiceClient {

    @PostMapping("/api/test/chunks")
    ResponseEntity<String> uploadChunks(@RequestBody VectorStoreRequestDto vectorStoreRequestDto);

    @DeleteMapping("/api/test/delete/{documentId}")
    ResponseEntity<String> deleteDocument(@PathVariable("documentId") String documentId);
}
