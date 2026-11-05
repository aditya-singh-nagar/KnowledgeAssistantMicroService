package com.example.RagService.service;

import com.example.RagService.dtos.DocumentChunkData;
import com.example.RagService.dtos.DocumentResponse;
import com.example.RagService.dtos.VectorStoreRequestDto;
import jakarta.transaction.Transactional;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VectorStoreService {

    private final VectorStore vectorStore;

    public VectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void saveChunks(List<Document> documents) {
        vectorStore.add(documents);
    }

    public List<Document> findByDocumentId(String documentId) {

        FilterExpressionBuilder builder =
                new FilterExpressionBuilder();

        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("document lookup")
                        .topK(100)
                        .filterExpression(
                                builder.eq("documentId", documentId).build()
                        )
                        .build()
        );
    }

    public void deleteChunksByDocumentId(String documentId) {

        FilterExpressionBuilder builder =
                new FilterExpressionBuilder();

        vectorStore.delete(
                builder.eq("documentId", documentId).build()
        );
    }

    @Transactional
    public String  saveDocumentChunks (VectorStoreRequestDto document) {


            List<org.springframework.ai.document.Document> vectorDocuments =
                    new ArrayList<>();

            for (DocumentChunkData chunk : document.getChunks()) {

                Map<String, Object> metadata = Map.of(
                        "documentId", document.getDocumentId(),
                        "fileName", document.getFileName(),
                        "chunkNumber", chunk.chunkNumber(),
                        "uploadDate", document.getUploadDate().toString(),
                        "documentType", document.getFileType() ,
                        "documentAcessLevel", document.getDocumentAcessLevel()
                );

                org.springframework.ai.document.Document vectorDocument =
                        new org.springframework.ai.document.Document(
                                chunk.content(),
                                metadata
                        );

                vectorDocuments.add(vectorDocument);
            }

            vectorStore.add(vectorDocuments);

            return "success" ;
    }


}
