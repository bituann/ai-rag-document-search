package com.bituan.ai_rag_document_search.service;

import com.bituan.ai_rag_document_search.exception.NoMatchFoundException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DocumentServiceImpl implements DocumentService{

    DocumentProcessingService documentProcessingService;
    VectorStore vectorStore;
    ChatClient chatClient;

    @Autowired
    public DocumentServiceImpl(DocumentProcessingService documentProcessingService, VectorStore vectorStore,
                               ChatClient chatClient) {
        this.documentProcessingService = documentProcessingService;
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    @Override
    public void uploadDocument(MultipartFile file) {
        List<Document> texts = documentProcessingService.extractText(file);
        List<Document> chunkedText = documentProcessingService.chunkText(texts);

        vectorStore.add(chunkedText);
    }

    @Override
    public String query(String query, int topK) {
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

        return chatClient.prompt(prompt).call().content();
    }
}
