package com.socialsentiment.backend.analysis;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class InstagramApiExceptionHandler {
    @ExceptionHandler(InstagramService.InstagramCollectionException.class)
    public ResponseEntity<Map<String, String>> instagramError(InstagramService.InstagramCollectionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
            .body(Map.of("error", "INSTAGRAM_API_ERROR", "message", ex.getMessage()));
    }
}
