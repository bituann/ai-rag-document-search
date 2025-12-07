package com.bituan.ai_rag_document_search.service;

import com.bituan.ai_rag_document_search.dto.response.DocumentResponse;
import com.bituan.ai_rag_document_search.dto.response.QueryResponse;
import com.bituan.ai_rag_document_search.exception.NoMatchFoundException;
import com.bituan.ai_rag_document_search.exception.ResourceNotFoundException;
import com.bituan.ai_rag_document_search.model.DocumentEntity;
import com.bituan.ai_rag_document_search.repository.DocumentRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService{

    DocumentRepository documentRepository;
    DocumentProcessingService documentProcessingService;
    VectorStore vectorStore;
    ChromaApi chromaVectorStore;
    ChatClient chatClient;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository, DocumentProcessingService documentProcessingService,
                               VectorStore vectorStore, ChromaApi chromaVectorStore,
                               ChatClient.Builder chatClient) {
        this.documentRepository = documentRepository;
        this.documentProcessingService = documentProcessingService;
        this.vectorStore = vectorStore;
        this.chromaVectorStore = chromaVectorStore;
        this.chatClient = chatClient.build();
    }

    @Override
    public DocumentEntity uploadDocument(MultipartFile file) {
        List<Document> texts = documentProcessingService.extractText(file);
        List<Document> chunkedText = documentProcessingService.chunkText(texts);

        Map<String, Object> metadata = chunkedText.get(0).getMetadata();
        metadata.put("source", file.getOriginalFilename());

        DocumentEntity doc = DocumentEntity.builder()
                .text(texts.stream().map(Document::getText).collect(Collectors.joining("\n\n")))
                .chunks(chunkedText.stream().map(chunk -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", chunk.getId());
                    map.put("text", chunk.getText());
                    return map;
                }).toList())
                .chunkCount(chunkedText.size())
                .metadata(metadata)
                .build();

        vectorStore.add(chunkedText);

        documentRepository.save(doc);

        return doc;
    }

    @Override
    public QueryResponse query(String query, int topK) {
        SearchRequest request = SearchRequest.builder().query(query).topK(topK).build();

        List<Document> similarDocuments = vectorStore.similaritySearch(request);

        if (similarDocuments.isEmpty()) {
            throw new NoMatchFoundException();
        }

        String context = similarDocuments.stream().map(Document::getText).collect(Collectors.joining("\n---\n"));

        String template = """
        Use the following context to answer the user's question.
        If you don't know the answer, just say that you don't know, don't try to make up an answer.
        ---
        {context}
        ---
        User question: {question}
        """;

        Prompt prompt = new PromptTemplate(template).create(
                Map.of("context", context, "question", query)
        );

        String answer = chatClient.prompt(prompt).call().content();
        List<Map<String, String>> chunks = similarDocuments.stream().map(doc -> {
            Map<String, String> map = new HashMap<>();
            map.put("id", doc.getId());
            map.put("text", doc.getText());
            return map;
        }).toList();
        Map<String, Double> similarityScores = similarDocuments.stream().collect(Collectors.toMap(Document::getId, Document::getScore));

        return QueryResponse.builder()
                .answer(answer)
                .chunks(chunks)
                .similarityScores(similarityScores)
                .build();
    }

    @Override
    public List<Map<String, Object>> getAllDocuments() {
        List<DocumentEntity> docs = documentRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (DocumentEntity doc : docs) {
            Map<String, Object> responseDoc = new HashMap<>();
            responseDoc.put("document_id", doc.getId());
            responseDoc.put("document_name", doc.getMetadata().get("source"));
            responseDoc.put("chunk_count", doc.getChunkCount());
            response.add(responseDoc);
        }

        return response;
    }

    @Override
    public DocumentResponse getDocument(UUID id) {
        DocumentEntity doc = documentRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        return DocumentResponse.builder()
                .text(doc.getText())
                .chunks(doc.getChunks())
                .metadata(doc.getMetadata())
                .build();
    }
}
