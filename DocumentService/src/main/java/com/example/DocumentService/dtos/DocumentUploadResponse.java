package com.example.DocumentService.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@RequiredArgsConstructor
public class DocumentUploadResponse {

    private String documentId;
    private String fileName;
    private boolean processed;
    private String message;
    private List<DocumentChunkData> chunks;





}
