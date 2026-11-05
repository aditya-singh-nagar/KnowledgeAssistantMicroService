package com.example.DocumentService.dtos;


import com.example.DocumentService.enums.DocumentAcessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private String documentId;
    private String fileName;
    private String filePath ;
    private String fileType;
    private boolean processed;
    private Long fileSize;
    private int totalChunks;
    private DocumentAcessLevel documentAcessLevel;
    private LocalDateTime uploadDate;
    private int uploadBy  ;







}
