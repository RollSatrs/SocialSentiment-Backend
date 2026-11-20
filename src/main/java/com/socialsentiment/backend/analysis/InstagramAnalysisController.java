package com.socialsentiment.backend.analysis;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/analyses")
@CrossOrigin(origins = "http://localhost:3000")
public class InstagramAnalysisController {
    private static final Pattern POST_PATH = Pattern.compile("^/(p|reel|tv)/[A-Za-z0-9_-]+/?$");

    public record AnalysisRequest(@NotBlank String url) {}
    public record AnalysisResponse(String status, String url, String message) {}

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AnalysisResponse analyze(@Valid @RequestBody AnalysisRequest request) {
        URI uri;
        try {
            uri = URI.create(request.url().trim());
        } catch (IllegalArgumentException ex) {
            throw new InvalidInstagramUrlException();
        }
        String host = uri.getHost();
        if (!"https".equalsIgnoreCase(uri.getScheme())
                || host == null
                || !(host.equalsIgnoreCase("instagram.com") || host.equalsIgnoreCase("www.instagram.com"))
                || uri.getRawUserInfo() != null
                || uri.getPort() != -1
                || !POST_PATH.matcher(uri.getPath()).matches()) {
            throw new InvalidInstagramUrlException();
        }
        String normalized = "https://www.instagram.com" + uri.getPath();
        return new AnalysisResponse("COLLECTOR_NOT_CONNECTED", normalized,
                "Ссылка проверена. Сбор реальных комментариев пока не подключён; результаты не выдумываются.");
    }

    @ExceptionHandler(InvalidInstagramUrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidUrl() {
        return Map.of("error", "INVALID_INSTAGRAM_URL", "message", "Укажите ссылку на публичный Instagram-пост или Reel.");
    }

    private static class InvalidInstagramUrlException extends RuntimeException {}
}
