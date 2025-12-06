package com.bituan.ai_rag_document_search.service;

import com.bituan.ai_rag_document_search.dto.response.QueryResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    void uploadDocument(MultipartFile file);
    QueryResponse query(String query, int topK);
}
