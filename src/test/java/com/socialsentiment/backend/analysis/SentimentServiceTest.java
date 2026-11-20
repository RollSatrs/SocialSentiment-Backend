package com.socialsentiment.backend.analysis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SentimentServiceTest {
    private final SentimentService service = new SentimentService();

    @Test void detectsNegative() { assertEquals("negative", service.classify("Ужасно, очень плохо")); }
    @Test void detectsPositive() { assertEquals("positive", service.classify("Отлично, спасибо!")); }
    @Test void detectsNeutral() { assertEquals("neutral", service.classify("Когда открытие?")); }
    @Test void detectsEnglish() { assertEquals("negative", service.classify("Terrible scam")); }
    @Test void rejectsBlank() { assertThrows(InvalidAnalysisException.class, () -> service.classify("  ")); }
}
