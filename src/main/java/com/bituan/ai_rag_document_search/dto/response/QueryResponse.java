package com.bituan.ai_rag_document_search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryResponse {
    private String answer;
    private List<Map<String, String>> chunks;
    private Map<String, Double> similarityScores;
}
