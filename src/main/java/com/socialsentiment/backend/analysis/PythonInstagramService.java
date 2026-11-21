package com.socialsentiment.backend.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;

@Service
public class PythonInstagramService {
    private final RestClient client;
    private final ObjectMapper mapper;

    public PythonInstagramService(ObjectMapper mapper,
            @Value("${app.parser.base-url:http://127.0.0.1:4002}") String baseUrl) {
        this.mapper = mapper;
        this.client = RestClient.builder().baseUrl(baseUrl)
                .requestFactory(new org.springframework.http.client.SimpleClientHttpRequestFactory() {{
                    setConnectTimeout(Duration.ofSeconds(5));
                    setReadTimeout(Duration.ofSeconds(90));
                }}).build();
    }

    public List<String> commentsForPost(String url) {
        try {
            String result = client.post().uri("/collect")
                    .body(new CollectRequest(url)).retrieve().body(String.class);
            JsonNode root = mapper.readTree(result);
            if (!root.path("comments").isArray()) {
                throw new InstagramService.InstagramCollectionException("Python parser returned invalid data.");
            }
            List<String> comments = new ArrayList<>();
            for (JsonNode node : root.path("comments")) {
                if (!node.isTextual()) continue;
                String value = node.asText().trim();
                if (!value.isBlank()) comments.add(value);
                if (comments.size() == 500) break;
            }
            return comments;
        } catch (InstagramService.InstagramCollectionException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new InstagramService.InstagramCollectionException(
                "Python parser unavailable or refused collection; check port 4002 and Scrapfly credits.");
        } catch (Exception ex) {
            throw new InstagramService.InstagramCollectionException("Python parser returned malformed data.");
        }
    }

    private record CollectRequest(String url) {}
}
