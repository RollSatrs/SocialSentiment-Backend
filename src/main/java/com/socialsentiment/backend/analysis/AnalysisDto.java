package com.socialsentiment.backend.analysis;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class AnalysisDto {
    private AnalysisDto() {}
    public record Request(@NotBlank String url, @Size(max = 500) List<@NotBlank String> comments, String source) {
        public Request(String url, List<String> comments) { this(url, comments, null); }
    }
    public record Comment(String text, String sentiment) {}
    public record Response(UUID id, String status, String url, String source, String message,
                           int total, int negative, int neutral, int positive,
                           Instant createdAt, List<Comment> comments) {}
}
