package com.bituan.ai_rag_document_search.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String text;

    private int chunkCount;

    private List<String> chunks;

    @ElementCollection
    @CollectionTable(name = "metadata", joinColumns = @JoinColumn(name = "metadata_id"))
    @MapKeyColumn(name = "metadata_key")
    @Column(name = "metadata_value")
    private Map<String, Object> metadata;
}
