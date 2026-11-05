package com.example.RagService.dtos;

import lombok.Data;

import java.nio.file.Path;

@Data

public class ProcessedDocument {

    private String processedFileName;
    private String processedFileType;
    private Path processedFilePath;
}
