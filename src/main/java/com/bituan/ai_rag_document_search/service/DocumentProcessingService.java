package com.bituan.ai_rag_document_search.service;

import org.springframework.ai.document.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentProcessingService {
    List<Document> extractText(MultipartFile file);
    List<Document> chunkText(List<Document> documents);
}
