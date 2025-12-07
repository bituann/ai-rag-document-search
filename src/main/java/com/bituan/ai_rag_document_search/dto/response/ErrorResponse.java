package com.bituan.ai_rag_document_search.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
@Builder
public class ErrorResponse {
    private String message;
}
