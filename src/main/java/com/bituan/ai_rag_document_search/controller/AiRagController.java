package com.bituan.ai_rag_document_search.controller;

import com.bituan.ai_rag_document_search.dto.response.ErrorResponse;
import com.bituan.ai_rag_document_search.dto.response.QueryResponse;
import com.bituan.ai_rag_document_search.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
@Tag(name = "AI-Powered Document Search & RAG Query Service")
public class AiRagController {
    DocumentService documentService;

    @Autowired
    AiRagController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a document. Supported formats: DOC, DOCX, PDF, TXT")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400")
    })
    public ResponseEntity<?> uploadDocument (MultipartFile file) {
        List<String> allowedExtensions = Arrays.asList("pdf", "doc", "docx", "txt");

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder().message("The document is empty. Please upload a valid document.").build());
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder().message("The document has an invalid filename. Please upload a valid document.").build());

        }

        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        // file extension validation
        if (!allowedExtensions.contains(fileExtension)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.builder().message("%s files are not accepted. Please upload a valid document format (DOCX, PDF, or TXT).". formatted(fileExtension)).build());
        }

        documentService.uploadDocument(file);

        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping("/query")
    @Operation( summary = "Send a query to get a response based on the uploaded documents")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Organizations retrieved successfully")
    })
    public ResponseEntity<QueryResponse> query (@RequestParam String query, @RequestParam int topK) {
        return ResponseEntity.ok(documentService.query(query, topK));
    }

    @GetMapping("/")
    @Operation( summary = "Get the details of all documents saved")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Organizations retrieved successfully")
    })
    public ResponseEntity<?> getAllDocuments () {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/{id}")
    @Operation( summary = "Get a specific document details by id")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Organizations retrieved successfully")
    })
    public ResponseEntity<?> getDocument (@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getDocument(id));
    }
}
