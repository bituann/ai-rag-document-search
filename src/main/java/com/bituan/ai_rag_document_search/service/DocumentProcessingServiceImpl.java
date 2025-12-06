package com.bituan.ai_rag_document_search.service;

import com.bituan.ai_rag_document_search.exception.ServerException;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentProcessingServiceImpl implements DocumentProcessingService {
    @Override
    public List<Document> extractText(MultipartFile file) {
        Resource resource;
        try {
            resource = new InputStreamResource(file.getInputStream());
        } catch (IOException e) {
            throw new ServerException(HttpStatusCode.valueOf(500) ,"");
        }
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        return reader.get();
    }

    @Override
    public List<Document> chunkText(List<Document> documents) {
        TokenTextSplitter textSplitter = new TokenTextSplitter(450, 350, 5, 10000, true);
        return textSplitter.apply(documents);
    }
}
