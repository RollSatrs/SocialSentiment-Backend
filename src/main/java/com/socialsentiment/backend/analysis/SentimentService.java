package com.socialsentiment.backend.analysis;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SentimentService {
    private static final Set<String> NEGATIVE = Set.of(
        "плохо", "ужас", "ужасно", "обман", "дорого", "отвратительно",
        "проблема", "жалоба", "кошмар", "ужасный", "bad", "terrible",
        "awful", "scam", "worst", "hate", "disappointed", "жаман", "нашар");
    private static final Set<String> POSITIVE = Set.of(
        "хорошо", "отлично", "супер", "класс", "спасибо", "нравится",
        "прекрасно", "лучший", "good", "great", "amazing", "love",
        "excellent", "thanks", "керемет", "жақсы", "рахмет");

    public String classify(String text) {
        if (text == null || text.isBlank()) throw new InvalidAnalysisException("Комментарий не может быть пустым.");
        Set<String> tokens = new HashSet<>(Arrays.asList(
            text.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", " ").trim().split("\\s+")));
        long negative = NEGATIVE.stream().filter(tokens::contains).count();
        long positive = POSITIVE.stream().filter(tokens::contains).count();
        return negative > positive ? "negative" : positive > negative ? "positive" : "neutral";
    }
}
