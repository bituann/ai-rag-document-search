package com.bituan.ai_rag_document_search.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    @Autowired
    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public List<Double> generateEmbedding(List<Document> doc) {
        return embeddingModel.embed(doc);
    }

    public List<List<Double>> generateEmbeddings(List<String> texts) {
        return embeddingModel.embed(texts);
    }
}
