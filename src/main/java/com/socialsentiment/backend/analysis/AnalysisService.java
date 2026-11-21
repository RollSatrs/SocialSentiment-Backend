package com.socialsentiment.backend.analysis;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class AnalysisService {
    private final AnalysisRepository repository;
    private final InstagramUrlService urls;
    private final SentimentService sentiment;
    private final InstagramService instagram;

    public AnalysisService(AnalysisRepository repository, InstagramUrlService urls, SentimentService sentiment, InstagramService instagram) {
        this.repository = repository; this.urls = urls; this.sentiment = sentiment; this.instagram = instagram;
    }

    @Transactional
    public AnalysisDto.Response analyze(AnalysisDto.Request request) {
        String url = urls.normalize(request.url());
        boolean automatic = request.comments() == null || request.comments().isEmpty();
        List<String> supplied = automatic ? instagram.commentsForPost(url) : request.comments();
        if (supplied.size() > 500) throw new InvalidAnalysisException("Не более 500 комментариев.");
        boolean hasComments = supplied.stream().anyMatch(s -> s != null && !s.isBlank());
        AnalysisEntity entity = new AnalysisEntity(url,
            "COMPLETED",
            automatic ? "instagram_api" : "user_supplied");
        for (String raw : supplied) {
            if (raw == null || raw.isBlank()) continue;
            String text = raw.trim();
            if (text.length() > 2000) throw new InvalidAnalysisException("Комментарий длиннее 2000 символов.");
            entity.addComment(new CommentEntity(text, sentiment.classify(text)));
        }
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public AnalysisDto.Response get(UUID id) {
        return toResponse(repository.findById(id).orElseThrow(
            () -> new AnalysisNotFoundException("Анализ не найден.")));
    }

    @Transactional(readOnly = true)
    public List<AnalysisDto.Response> recent() {
        return repository.findAll().stream()
            .sorted(Comparator.comparing(AnalysisEntity::getCreatedAt).reversed())
            .limit(20).map(this::toResponse).toList();
    }

    private AnalysisDto.Response toResponse(AnalysisEntity entity) {
        List<AnalysisDto.Comment> comments = entity.getComments().stream()
            .map(c -> new AnalysisDto.Comment(c.getText(), c.getSentiment())).toList();
        int negative = (int) comments.stream().filter(c -> c.sentiment().equals("negative")).count();
        int positive = (int) comments.stream().filter(c -> c.sentiment().equals("positive")).count();
        int neutral = comments.size() - negative - positive;
        String message = comments.isEmpty()
            ? "Комментарии не найдены или недоступны для этой публикации."
            : "Анализ комментариев выполнен словарным классификатором; это не AI-модель.";
        return new AnalysisDto.Response(entity.getId(), entity.getStatus(), entity.getPostUrl(),
            entity.getSource(), message, comments.size(), negative, neutral, positive,
            entity.getCreatedAt(), comments);
    }
}
