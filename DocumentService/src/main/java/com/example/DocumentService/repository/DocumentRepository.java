package com.example.DocumentService.repository;



import com.example.DocumentService.entities.Document;
import com.example.DocumentService.enums.DocumentAcessLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository
        extends JpaRepository<Document, String> {

    List<Document> findAllByAcessLevel(DocumentAcessLevel accessLevel);
    List<Document> findAllByAcessLevelNot(DocumentAcessLevel accessLevel);
}
