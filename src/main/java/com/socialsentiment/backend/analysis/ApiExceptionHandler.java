package com.socialsentiment.backend.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler({InvalidAnalysisException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> badRequest(Exception ex) {
        log.info("Invalid analysis request: {}", ex.getMessage());
        String message = ex instanceof InvalidAnalysisException ? ex.getMessage() : "Некорректные данные запроса.";
        return ResponseEntity.badRequest().body(Map.of("error", "INVALID_INPUT", "message", message));
    }

    @ExceptionHandler(AnalysisNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(AnalysisNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "NOT_FOUND", "message", ex.getMessage()));
    }
}
