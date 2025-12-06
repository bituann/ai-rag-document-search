package com.bituan.ai_rag_document_search.service;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentProcessingService {
    String extractText(MultipartFile file);
    void chunkText(String text);
    void generateEmbeddings(String text);
}
