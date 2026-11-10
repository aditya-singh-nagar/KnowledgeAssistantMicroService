package com.example.DocumentService.service;


import com.example.DocumentService.configuration.RagServiceClient;
import com.example.DocumentService.configuration.UserServiceClient;
import com.example.DocumentService.customException.AppException;
import com.example.DocumentService.customException.NotFound;
import com.example.DocumentService.dtos.*;
import com.example.DocumentService.entities.Document;
import com.example.DocumentService.enums.DocumentAcessLevel;
import com.example.DocumentService.enums.SystemCode;
import com.example.DocumentService.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DocumentService {

    private final FileStorageService fileStorageService;
    private final TextExtractionService textExtractionService;
    private final ChunkingService chunkingService;
    private final DocumentRepository documentRepository;
    private final UserServiceClient  userServiceClient;
    private final RagServiceClient ragServiceClient;





    private static final Set<String> ALLOWED_FILE_TYPES = Set.of(
            "application/pdf",
            "text/plain",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword"
    );




    @Transactional
    public DocumentUploadResponse uploadDocument(
            MultipartFile file , String email , DocumentAcessLevel acessLevel ,String role) {

        validateFile(file);

        String storedFilePath = null;



        try {


            String fileType =
                    textExtractionService.detectFileType(file);


            validateFileType(fileType);

            OriginalDocument originalDocument = fileStorageService.store(file);


            storedFilePath = originalDocument.getProcessedFilePath().toString();

            String fileName = originalDocument.getProcessedFileName() ;

            String extractedText =
                    textExtractionService.extractText(file);

            if (extractedText == null ||
                    extractedText.isBlank()) {

                throw new AppException(
                        SystemCode.DOC_EMPTY_FILE,
                        "No readable text found in document"
                );
            }


            List<DocumentChunkData> chunks = chunkingService.createChunksWithOverlap(extractedText) ;

            Document document = new Document();

            document.setId(
                    UUID.randomUUID().toString()
            );

            document.setFileName(
                    fileName
            );

            document.setFileType(fileType);

            document.setFilePath(
                    storedFilePath
            );

            document.setFileSize(
                    file.getSize()
            );

            document.setProcessed(true);

            document.setTotalChunks(chunks.size());

            ResponseEntity<RegisterResponseDto> entity = userServiceClient.getUserByEmail(email) ;
                RegisterResponseDto registerResponseDto = entity.getBody() ;
                document.setUploadBy(registerResponseDto.getId());




            document.setUploadBy(registerResponseDto.getId());




            String userRole = role.substring(5) ;




            if(userRole.equals("ADMIN")){
                document.setAcessLevel(acessLevel);
            }
            else if(userRole.equals("EMPLOYEE")){

                boolean  isvalid =   acessLevel.equals(DocumentAcessLevel.EMPLOYEE )||acessLevel.equals(DocumentAcessLevel.PUBLIC) ;
                if(isvalid){
                    document.setAcessLevel(acessLevel);
                }else {

                    throw new IllegalArgumentException("invalid acess level");
                }
            }

            else {
                boolean  isvalid =   !acessLevel.equals(DocumentAcessLevel.ADMIN);

                if (isvalid){
                    document.setAcessLevel(acessLevel);
                }
                else {throw new IllegalArgumentException("Invalid acess level");}
            }


           Document savedDocument =  documentRepository.save(document);

            VectorStoreRequestDto documentResponse = new VectorStoreRequestDto();

            documentResponse.setDocumentId(document.getId());
        documentResponse.setFileName(document.getFileName());
        documentResponse.setTotalChunks(document.getTotalChunks());
        documentResponse.setUploadDate(document.getUploadDate());
        documentResponse.setFileType(document.getFileType());
        documentResponse.setProcessed(document.isProcessed());
        documentResponse.setDocumentAcessLevel(document.getAcessLevel());
        documentResponse.setFileSize(document.getFileSize());
        documentResponse.setFilePath(document.getFilePath());
        documentResponse.setChunks(chunks);

       ResponseEntity<String> responseEntity = ragServiceClient.uploadChunks(documentResponse);


           return new DocumentUploadResponse(
                   document.getId(),
                   document.getFileName(),
                   document.isProcessed(),
                   "Document uploaded and processed successfully",
                   chunks );












        } catch (Exception e) {

            // If DB/Tika/chunking fails after file storage,
            // remove the orphaned original file.
            if (storedFilePath != null) {
                try {
                    fileStorageService.delete(
                            storedFilePath
                    );
                } catch (Exception ignored) {
                    // Log this in production
                }
            }

            throw new AppException(
                    SystemCode.DOC_PROCESSING_FAILED ,
                    "Document processing failed"
            );
        }
    }


    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new AppException(
                    SystemCode.DOC_EMPTY_FILE,
                    "File cannot be empty"
            );
        }
    }

    private void validateFileType(String fileType) {

        if (!ALLOWED_FILE_TYPES.contains(fileType)) {
            throw new AppException(
                    SystemCode.DOC_INVALID_FILE_TYPE,
                    "File type not supported. Only PDF, DOCX and TXT are allowed."
            );
        }
    }



    public DocumentResponse getDocumentById(String id , String role) {

        Document document = documentRepository.findById(id).orElseThrow(()-> new NotFound(SystemCode.DOC_NOT_FOUND, "Document not found with the given ID"));

        String userRole = role.substring(5) ;
        DocumentAcessLevel acessLevel = document.getAcessLevel();


        if(userRole.equals("ADMIN")){
            return getDocumentResponse(document);
        }
        else if(userRole.equals("EMPLOYEE")){

            boolean  isvalid =   acessLevel.equals(DocumentAcessLevel.EMPLOYEE )||acessLevel.equals(DocumentAcessLevel.PUBLIC) ;
            if(isvalid){
                return getDocumentResponse(document);
            }else {

                throw new AppException(SystemCode.DOC_ACCESS_DENIED, "Access denied: your role is not authorized to view this document");
            }
        } else if (userRole.equals("PUBLIC")) {

            boolean  isvalid =   acessLevel.equals(DocumentAcessLevel.PUBLIC);
            if(isvalid){
                return getDocumentResponse(document);
            }else  {
                throw new AppException(SystemCode.DOC_ACCESS_DENIED, "Access denied: your role is not authorized to view this document");
            }

        } else {
            boolean  isvalid =   !acessLevel.equals(DocumentAcessLevel.ADMIN);

            if (isvalid){
                return getDocumentResponse(document);
            }
            else {throw new AppException(SystemCode.DOC_ACCESS_DENIED, "Access denied: your role is not authorized to view this document");}
        }


    }



    public List<DocumentResponse> getAllDocuments(String role) {



        DocumentAcessLevel userRole = DocumentAcessLevel.valueOf(role);



        if(userRole.equals(DocumentAcessLevel.PUBLIC)){
            List<Document> documents = documentRepository.findAllByAcessLevel(userRole);
            return documents.stream().map(this::getDocumentResponse).collect(Collectors.toList()) ;
        }
        else if (userRole.equals(DocumentAcessLevel.EMPLOYEE)){

            List<Document> documents1 = documentRepository.findAllByAcessLevel(DocumentAcessLevel.PUBLIC);
            List<Document> documents2 = documentRepository.findAllByAcessLevel(DocumentAcessLevel.EMPLOYEE);

            List<Document> allDocuments = new ArrayList<>();
            allDocuments.addAll(documents1);
            allDocuments.addAll(documents2);
            return allDocuments.stream().map(this::getDocumentResponse).collect(Collectors.toList()) ;
        }

        else if (userRole.equals(DocumentAcessLevel.MANAGER)){
            List<Document> documents = documentRepository.findAllByAcessLevelNot(DocumentAcessLevel.ADMIN);
            return documents.stream().map(this::getDocumentResponse).collect(Collectors.toList()) ;
        }

        else {

            List<Document> documents = documentRepository.findAll();
            return documents.stream().map(this::getDocumentResponse).collect(Collectors.toList()) ;
        }


    }



    @Transactional
    public void deleteDocumentById(String id , String role) {

        Document document = documentRepository.findById(id).orElseThrow(()-> new NotFound(SystemCode.DOC_NOT_FOUND, "Document not found with the given ID"));


            if(role.equals("ROLE_ADMIN")){
                try{
                    deletePhysicalFileAndDocumentChunks(document.getFilePath(), document.getId());
                    documentRepository.deleteById(id);
                }catch(Exception e){

                    System.out.println(e);
                }
            }else {

                if(document.getAcessLevel().equals(DocumentAcessLevel.ADMIN)){
                    throw new AppException(SystemCode.DOC_ACCESS_DENIED, "Access denied: managers cannot delete ADMIN documents");
                }else {
                    try{
                        deletePhysicalFileAndDocumentChunks(document.getFilePath(), document.getId());
                        documentRepository.deleteById(id);
                    }catch(Exception e){

                        System.out.println(e);
                    }
                }
            }


    }



    public void deletePhysicalFileAndDocumentChunks(String originalFilePath  , String documentId) throws IOException {
        Path originalpath = Path.of(originalFilePath);

        Files.deleteIfExists(originalpath);

       ResponseEntity<String> entity = ragServiceClient.deleteDocument(documentId);



    }


    public DocumentResponse getDocumentResponse (Document document) {
        DocumentResponse documentResponse = new DocumentResponse();
        documentResponse.setDocumentId(document.getId());
        documentResponse.setFileName(document.getFileName());
        documentResponse.setTotalChunks(document.getTotalChunks());
        documentResponse.setUploadDate(document.getUploadDate());
        documentResponse.setFileType(document.getFileType());
        documentResponse.setProcessed(document.isProcessed());
        documentResponse.setDocumentAcessLevel(document.getAcessLevel());
        documentResponse.setFileSize(document.getFileSize());
        documentResponse.setFilePath(document.getFilePath());
        documentResponse.setUploadBy(document.getUploadBy());

        return documentResponse;
    }


}

