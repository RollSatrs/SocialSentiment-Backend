package com.socialsentiment.backend.analysis;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "analyses")
public class AnalysisEntity {
    @Id
    private UUID id = UUID.randomUUID();
    @Column(nullable = false, length = 500)
    private String postUrl;
    @Column(nullable = false, length = 32)
    private String status;
    @Column(nullable = false, length = 32)
    private String source;
    @Column(nullable = false)
    private Instant createdAt = Instant.now();
    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>();

    protected AnalysisEntity() {}
    public AnalysisEntity(String postUrl, String status, String source) {
        this.postUrl = postUrl; this.status = status; this.source = source;
    }
    public void addComment(CommentEntity comment) {
        comments.add(comment);
        comment.setAnalysis(this);
    }
    public UUID getId() { return id; }
    public String getPostUrl() { return postUrl; }
    public String getStatus() { return status; }
    public String getSource() { return source; }
    public Instant getCreatedAt() { return createdAt; }
    public List<CommentEntity> getComments() { return comments; }
}
