package com.socialsentiment.backend.analysis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.time.Duration;
import java.util.List;

@Service
public class AiSentimentService {
    private final RestClient client;
    public AiSentimentService(@Value("${app.parser.base-url:http://127.0.0.1:4002}") String baseUrl) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofMinutes(5));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }
    public List<String> classify(List<String> texts) {
        if (texts.isEmpty()) return List.of();
        try {
            SentimentResponse response = client.post().uri("/sentiment")
                .body(new SentimentRequest(texts)).retrieve().body(SentimentResponse.class);
            if (response == null || response.results() == null || response.results().size() != texts.size())
                throw new IllegalStateException("Invalid sentiment response size");
            return response.results().stream().map(item -> {
                if (item == null || !List.of("negative", "neutral", "positive").contains(item.sentiment()))
                    throw new IllegalStateException("Invalid sentiment label");
                return item.sentiment();
            }).toList();
        } catch (RestClientException | IllegalStateException ex) {
            throw new InstagramService.InstagramCollectionException(
                "AI sentiment service unavailable; install Python model dependencies and check port 4002.");
        }
    }
    private record SentimentRequest(List<String> texts) {}
    private record SentimentResponse(List<SentimentResult> results) {}
    private record SentimentResult(String sentiment, double confidence) {}
}
