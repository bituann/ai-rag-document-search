package com.bituan.ai_rag_document_search.exception;

import org.springframework.http.HttpStatusCode;

public class ServerException extends RuntimeException {
    HttpStatusCode code;
    String message;

    public ServerException(HttpStatusCode code, String message) {
        this.code = code;
        this.message = message;
    }

}
