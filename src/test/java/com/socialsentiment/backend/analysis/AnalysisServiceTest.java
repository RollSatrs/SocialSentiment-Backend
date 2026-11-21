package com.socialsentiment.backend.analysis;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalysisServiceTest {
    private final AnalysisRepository repository = mock(AnalysisRepository.class);
    private final InstagramService instagram = mock(InstagramService.class);
    private final PythonInstagramService parser = mock(PythonInstagramService.class);
    private final AiSentimentService ai = mock(AiSentimentService.class);
    private final AnalysisService service = new AnalysisService(
        repository, new InstagramUrlService(), new SentimentService(), instagram, parser, ai, "dictionary");

    @Test void persistsAnalysisWithComments() {
        when(repository.save(any(AnalysisEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.analyze(new AnalysisDto.Request(
            "https://www.instagram.com/p/ABC123/", List.of("Ужасно", "Отлично", "Когда?")));
        assertEquals("COMPLETED", response.status());
        assertEquals("user_supplied", response.source());
        assertEquals(3, response.total());
        assertEquals(1, response.negative());
        assertEquals(1, response.positive());
        verify(repository).save(any(AnalysisEntity.class));
        verifyNoInteractions(instagram, parser);
    }

    @Test void routesScrapflyRequestsToPython() {
        when(parser.commentsForPost(anyString())).thenReturn(List.of("Отлично"));
        when(repository.save(any(AnalysisEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.analyze(new AnalysisDto.Request(
            "https://instagram.com/reel/ABC123/", List.of(), "scrapfly"));
        assertEquals("scrapfly", response.source());
        assertEquals(1, response.total());
        assertEquals(1, response.positive());
        verify(parser).commentsForPost("https://www.instagram.com/reel/ABC123/");
        verifyNoInteractions(instagram);
    }

    @Test void defaultsToMetaGraphApi() {
        when(instagram.commentsForPost(anyString())).thenReturn(List.of("Когда?"));
        when(repository.save(any(AnalysisEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.analyze(new AnalysisDto.Request(
            "https://instagram.com/p/ABC123/", List.of()));
        assertEquals("instagram_api", response.source());
        verify(instagram).commentsForPost("https://www.instagram.com/p/ABC123/");
        verifyNoInteractions(parser);
    }

    @Test void reanalyzesSavedCommentsWithoutScraping() {
        UUID id = UUID.randomUUID();
        AnalysisEntity original = new AnalysisEntity("https://www.instagram.com/p/ABC123/", "COMPLETED", "scrapfly");
        original.addComment(new CommentEntity("Отвратительное место", "neutral"));
        original.addComment(new CommentEntity("Спасибо!", "positive"));
        when(repository.findById(id)).thenReturn(Optional.of(original));
        when(ai.classify(List.of("Отвратительное место", "Спасибо!")))
            .thenReturn(List.of("negative", "positive"));
        when(repository.save(any(AnalysisEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.reanalyzeWithAi(id);
        assertEquals("reanalyzed_ai", response.source());
        assertEquals(2, response.total());
        assertEquals(1, response.negative());
        assertEquals(1, response.positive());
        verifyNoInteractions(instagram, parser);
        assertEquals("neutral", original.getComments().get(0).getSentiment());
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
