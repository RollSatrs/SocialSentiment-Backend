package com.socialsentiment.backend.analysis;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/analyses")
@CrossOrigin(origins = "http://localhost:4000")
public class InstagramAnalysisController {
    private static final Pattern POST_PATH = Pattern.compile("^/(p|reel|tv)/[A-Za-z0-9_-]+/?$");
    private static final Set<String> NEGATIVE = Set.of("плохо", "ужас", "ужасно", "обман", "дорого", "отвратительно", "негатив", "проблема", "жалоба", "некачественно", "кошмар", "ужасный", "bad", "terrible", "awful", "scam", "worst", "hate", "poor", "disappointed", "жаман", "нашар", "алдау");
    private static final Set<String> POSITIVE = Set.of("хорошо", "отлично", "супер", "класс", "спасибо", "нравится", "прекрасно", "лучший", "good", "great", "amazing", "love", "excellent", "thanks", "керемет", "жақсы", "рахмет");

    public record AnalysisRequest(@NotBlank String url, List<String> comments) {}
    public record CommentResult(String text, String sentiment) {}
    public record AnalysisResponse(String status, String url, String source, String message,
                                   int total, int negative, int neutral, int positive, List<CommentResult> comments) {}

    @PostMapping
    public AnalysisResponse analyze(@Valid @RequestBody AnalysisRequest request) {
        String normalized = normalize(request.url());
        List<String> submitted = request.comments() == null ? List.of() : request.comments();
        if (submitted.isEmpty()) {
            return new AnalysisResponse("COLLECTOR_NOT_CONNECTED", normalized, "none",
                "Instagram не предоставляет гарантированный публичный доступ к комментариям. Автоматический сбор пока недоступен. Вставьте реальные комментарии вручную для анализа.",
                0, 0, 0, 0, List.of());
        }
        if (submitted.size() > 500) throw new InvalidInputException("Не более 500 комментариев за один запрос.");
        List<CommentResult> results = new ArrayList<>();
        int negative = 0, positive = 0, neutral = 0;
        for (String raw : submitted) {
            if (raw == null || raw.isBlank()) continue;
            String comment = raw.trim();
            if (comment.length() > 2000) throw new InvalidInputException("Комментарий длиннее 2000 символов.");
            int score = score(comment, NEGATIVE) * -1 + score(comment, POSITIVE);
            String sentiment = score < 0 ? "negative" : score > 0 ? "positive" : "neutral";
            if (score < 0) negative++; else if (score > 0) positive++; else neutral++;
            results.add(new CommentResult(comment, sentiment));
        }
        return new AnalysisResponse("COMPLETED", normalized, "user_supplied",
            "Анализ предоставленных комментариев выполнен. Это словарная классификация, не AI-модель; Instagram-парсер ещё не подключён.",
            results.size(), negative, neutral, positive, results);
    }

    private static int score(String comment, Set<String> words) {
        String cleaned = comment.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", " ");
        Set<String> tokens = new HashSet<>(Arrays.asList(cleaned.split("\\s+")));
        return (int) words.stream().filter(tokens::contains).count();
    }

    private static String normalize(String url) {
        try {
            URI uri = URI.create(url.trim());
            String host = uri.getHost();
            if (!"https".equalsIgnoreCase(uri.getScheme()) || host == null
                || !(host.equalsIgnoreCase("instagram.com") || host.equalsIgnoreCase("www.instagram.com"))
                || uri.getRawUserInfo() != null || uri.getPort() != -1
                || !POST_PATH.matcher(uri.getPath()).matches()) throw new IllegalArgumentException();
            return "https://www.instagram.com" + uri.getPath();
        } catch (Exception ex) {
            throw new InvalidInputException("Укажите корректную HTTPS-ссылку на Instagram-пост или Reel.");
        }
    }

    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidInput(InvalidInputException ex) {
        return Map.of("error", "INVALID_INPUT", "message", ex.getMessage());
    }

    private static class InvalidInputException extends RuntimeException {
        InvalidInputException(String message) { super(message); }
    }
}
