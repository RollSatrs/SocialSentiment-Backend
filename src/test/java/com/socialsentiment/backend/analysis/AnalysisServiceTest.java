package com.socialsentiment.backend.analysis;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalysisServiceTest {
    private final AnalysisRepository repository = mock(AnalysisRepository.class);
    private final AnalysisService service = new AnalysisService(repository, new InstagramUrlService(), new SentimentService());

    @Test void persistsAnalysisWithComments() {
        when(repository.save(any(AnalysisEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.analyze(new AnalysisDto.Request(
            "https://www.instagram.com/p/ABC123/", List.of("Ужасно", "Отлично", "Когда?")));
        assertEquals("COMPLETED", response.status());
        assertEquals(3, response.total());
        assertEquals(1, response.negative());
        assertEquals(1, response.positive());
        verify(repository).save(any(AnalysisEntity.class));
    }

    @Test void neverFabricatesInstagramComments() {
        when(repository.save(any(AnalysisEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.analyze(new AnalysisDto.Request("https://instagram.com/reel/ABC123/", List.of()));
        assertEquals("COLLECTOR_NOT_CONNECTED", response.status());
        assertEquals(0, response.total());
    }

    @Test void rejectsInvalidUrl() {
        assertThrows(InvalidAnalysisException.class,
            () -> service.analyze(new AnalysisDto.Request("https://evil.example/p/ABC123/", List.of())));
        verifyNoInteractions(repository);
    }

    @Test void missingAnalysisReturnsNotFound() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(AnalysisNotFoundException.class, () -> service.get(UUID.randomUUID()));
    }
}
