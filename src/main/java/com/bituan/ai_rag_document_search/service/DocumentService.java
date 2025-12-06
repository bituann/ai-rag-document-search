package com.bituan.ai_rag_document_search.service;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    void uploadDocument(MultipartFile file);
    String query(String query, int topK);
}
