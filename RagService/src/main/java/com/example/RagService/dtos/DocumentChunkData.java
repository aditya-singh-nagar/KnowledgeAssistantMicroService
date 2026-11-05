package com.example.RagService.dtos;

public record DocumentChunkData(
        String content,
        int chunkNumber
) {
}
