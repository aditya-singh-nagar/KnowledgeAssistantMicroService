package com.example.RagService.dtos;

import com.example.RagService.enums.DocumentAcessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class VectorStoreRequestDto {

    private String documentId;
    private String fileName;
    private String filePath ;
    private String fileType;
    private boolean processed;
    private Long fileSize;
    private int totalChunks;
    private DocumentAcessLevel documentAcessLevel;
    private LocalDateTime uploadDate;
    private RegisterResponseDto registerResponseDto ;

    List<DocumentChunkData> chunks ;


}
