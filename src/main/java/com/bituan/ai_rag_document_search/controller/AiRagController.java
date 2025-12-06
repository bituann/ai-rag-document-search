package com.bituan.ai_rag_document_search.controller;

import com.bituan.ai_rag_document_search.service.DocumentProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController("/documents")
public class AiRagController {
    DocumentProcessingService documentProcessingService;

    @Autowired
    AiRagController(DocumentProcessingService documentProcessingService) {
        this.documentProcessingService = documentProcessingService;
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

        String documentText = documentProcessingService.extractText(file);

        return ResponseEntity.ok().build();
    }
}
