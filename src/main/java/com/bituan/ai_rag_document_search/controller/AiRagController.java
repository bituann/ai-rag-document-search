package com.bituan.ai_rag_document_search.controller;

import com.bituan.ai_rag_document_search.dto.request.QueryRequest;
import com.bituan.ai_rag_document_search.dto.response.QueryResponse;
import com.bituan.ai_rag_document_search.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController("/documents")
public class AiRagController {
    DocumentService documentService;

    @Autowired
    AiRagController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument (MultipartFile file) {
        List<String> allowedExtensions = Arrays.asList("pdf", "doc", "docx", "txt");

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a valid document.");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a valid document.");
        }

        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        // file extension validation
        if (!allowedExtensions.contains(fileExtension)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("%s files are not accepted. Please upload a valid document format.". formatted(fileExtension));
        }

        documentService.uploadDocument(file);

        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping("/query")
    public ResponseEntity<QueryResponse> query (@RequestParam QueryRequest request) {
        return ResponseEntity.ok(documentService.query(request.getQuery(), request.getTopK()));
    }

    @GetMapping
    public ResponseEntity<?> getAllDocuments () {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDocument (@RequestParam UUID id) {
        return ResponseEntity.ok(documentService.getDocument(id));
    }
}
