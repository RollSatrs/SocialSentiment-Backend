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
    private final PythonInstagramService parser;
    private final AiSentimentService ai;
    private final String sentimentMode;

    public AnalysisService(AnalysisRepository repository, InstagramUrlService urls,
            SentimentService sentiment, InstagramService instagram, PythonInstagramService parser,
            AiSentimentService ai, @org.springframework.beans.factory.annotation.Value("${app.sentiment.mode:ai}") String sentimentMode) {
        this.repository = repository;
        this.urls = urls;
        this.sentiment = sentiment;
        this.instagram = instagram;
        this.parser = parser;
        this.ai = ai;
        this.sentimentMode = sentimentMode;
    }

    @Transactional
    public AnalysisDto.Response analyze(AnalysisDto.Request request) {
        String url = urls.normalize(request.url());
        boolean automatic = request.comments() == null || request.comments().isEmpty();
        String source = "user_supplied";
        List<String> supplied;
        if (!automatic) {
            supplied = request.comments();
        } else if ("scrapfly".equalsIgnoreCase(request.source())) {
            supplied = parser.commentsForPost(url);
            source = "scrapfly";
        } else if (request.source() == null || request.source().isBlank()
                || "instagram_api".equalsIgnoreCase(request.source())) {
            supplied = instagram.commentsForPost(url);
            source = "instagram_api";
        } else {
            throw new InvalidAnalysisException("Допустимые источники: instagram_api, scrapfly.");
        }
        if (supplied.size() > 500) throw new InvalidAnalysisException("Не более 500 комментариев.");
        List<String> texts = new ArrayList<>();
        for (String raw : supplied) {
            if (raw == null || raw.isBlank()) continue;
            String text = raw.trim();
            if (text.length() > 2000) throw new InvalidAnalysisException("Комментарий длиннее 2000 символов.");
            texts.add(text);
        }
        List<String> labels;
        if ("ai".equalsIgnoreCase(sentimentMode)) labels = ai.classify(texts);
        else if ("dictionary".equalsIgnoreCase(sentimentMode)) labels = texts.stream().map(sentiment::classify).toList();
        else throw new IllegalStateException("Unknown app.sentiment.mode");
        AnalysisEntity entity = new AnalysisEntity(url, "COMPLETED", source);
        for (int i = 0; i < texts.size(); i++) entity.addComment(new CommentEntity(texts.get(i), labels.get(i)));
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
            : "ai".equalsIgnoreCase(sentimentMode)
                ? "Анализ выполнен мультиязычной моделью XLM-RoBERTa; результаты вероятностные."
                : "Анализ выполнен словарным классификатором; это не AI-модель.";
        return new AnalysisDto.Response(entity.getId(), entity.getStatus(), entity.getPostUrl(),
            entity.getSource(), message, comments.size(), negative, neutral, positive,
            entity.getCreatedAt(), comments);
    }
}
