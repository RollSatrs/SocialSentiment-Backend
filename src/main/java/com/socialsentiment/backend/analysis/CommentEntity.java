package com.socialsentiment.backend.analysis;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "comments")
public class CommentEntity {
    @Id
    private UUID id = UUID.randomUUID();
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_id", nullable = false)
    private AnalysisEntity analysis;
    @Column(nullable = false, length = 2000)
    private String text;
    @Column(nullable = false, length = 16)
    private String sentiment;

    protected CommentEntity() {}
    public CommentEntity(String text, String sentiment) {
        this.text = text; this.sentiment = sentiment;
    }
    void setAnalysis(AnalysisEntity analysis) { this.analysis = analysis; }
    public UUID getId() { return id; }
    public String getText() { return text; }
    public String getSentiment() { return sentiment; }
}
