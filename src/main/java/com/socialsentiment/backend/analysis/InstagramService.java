package com.socialsentiment.backend.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.net.URI;
import java.util.*;

@Service
public class InstagramService {
    private final RestClient client;
    private final ObjectMapper mapper;
    private final String token;
    private final String apiVersion;

    public InstagramService(ObjectMapper mapper,
                            @Value("${INSTAGRAM_ACCESS_TOKEN:}") String token,
                            @Value("${app.instagram.api-version:v24.0}") String apiVersion) {
        this.mapper = mapper;
        this.token = token;
        this.apiVersion = apiVersion;
        this.client = RestClient.builder().baseUrl("https://graph.facebook.com").build();
    }

    public List<String> commentsForPost(String normalizedUrl) {
        if (token.isBlank()) throw new InstagramCollectionException("INSTAGRAM_ACCESS_TOKEN не настроен на сервере.");
        try {
            String target = stripTrailingSlash(normalizedUrl);
            String mediaId = null;
            String next = "/" + apiVersion + "/me/media?fields=id,permalink&limit=100";
            Set<String> visited = new HashSet<>();
            for (int page = 0; page < 10 && next != null; page++) {
                if (!visited.add(next)) break;
                JsonNode data = request(next);
                for (JsonNode media : data.path("data")) {
                    if (target.equals(stripTrailingSlash(media.path("permalink").asText()))) {
                        mediaId = media.path("id").asText();
                        break;
                    }
                }
                if (mediaId != null) break;
                next = safeNext(data.path("paging").path("next").asText(null));
            }
            if (mediaId == null) throw new InstagramCollectionException(
                "Публикация не найдена среди доступных медиа подключённого Instagram-аккаунта.");
            List<String> comments = new ArrayList<>();
            next = "/" + apiVersion + "/" + mediaId + "/comments?fields=id,text&limit=100";
            visited.clear();
            for (int page = 0; page < 10 && next != null && comments.size() < 500; page++) {
                if (!visited.add(next)) break;
                JsonNode data = request(next);
                for (JsonNode comment : data.path("data")) {
                    String text = comment.path("text").asText("");
                    if (!text.isBlank()) comments.add(text);
                    if (comments.size() == 500) break;
                }
                next = safeNext(data.path("paging").path("next").asText(null));
            }
            return comments;
        } catch (RestClientResponseException ex) {
            throw new InstagramCollectionException("Meta API отклонил запрос (HTTP " + ex.getStatusCode().value() +
                "). Проверьте разрешения токена и доступ к Instagram-аккаунту.");
        } catch (InstagramCollectionException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InstagramCollectionException("Не удалось получить комментарии через Meta API.");
        }
    }

    private JsonNode request(String path) throws Exception {
        String separator = path.contains("?") ? "&" : "?";
        String json = client.get().uri(path + separator + "access_token=" +
            java.net.URLEncoder.encode(token, java.nio.charset.StandardCharsets.UTF_8))
            .retrieve().body(String.class);
        return mapper.readTree(json);
    }

    private String safeNext(String next) {
        if (next == null || next.isBlank()) return null;
        URI uri = URI.create(next);
        if (!"https".equals(uri.getScheme()) || !"graph.facebook.com".equals(uri.getHost()))
            throw new InstagramCollectionException("Meta API вернул недопустимую ссылку пагинации.");
        // Drop access_token from the pagination URL; use the server-side token instead.
        String query = uri.getRawQuery();
        String filtered = query == null ? "" : Arrays.stream(query.split("&"))
            .filter(p -> !p.startsWith("access_token=")).reduce((a,b) -> a + "&" + b).orElse("");
        return uri.getRawPath() + (filtered.isBlank() ? "" : "?" + filtered);
    }

    private String stripTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    public static class InstagramCollectionException extends RuntimeException {
        public InstagramCollectionException(String message) { super(message); }
    }
}
