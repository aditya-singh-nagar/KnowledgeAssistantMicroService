package com.example.DocumentService.dtos;

public record DocumentChunkData(
        String content,
        int chunkNumber
) {
}
