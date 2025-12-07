package com.bituan.ai_rag_document_search.service;

import com.bituan.ai_rag_document_search.dto.response.DocumentResponse;
import com.bituan.ai_rag_document_search.dto.response.QueryResponse;
import com.bituan.ai_rag_document_search.model.DocumentEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DocumentService {
    DocumentEntity uploadDocument(MultipartFile file);
    QueryResponse query(String query, int topK);
    List<Map<String, Object>> getAllDocuments();
    DocumentResponse getDocument(UUID id);
}
