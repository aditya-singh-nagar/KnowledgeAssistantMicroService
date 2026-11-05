package com.example.DocumentService.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TextExtractionService {

    private final Tika tika;

    public TextExtractionService(Tika tika) {
        this.tika = tika;
    }

    public String extractText(MultipartFile file) {
        try {
            return tika.parseToString(file.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to extract text from document", e
            );
        }
    }

    public String detectFileType(MultipartFile file) {
        try {
            return tika.detect(file.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to detect document type", e
            );
        }
    }


}