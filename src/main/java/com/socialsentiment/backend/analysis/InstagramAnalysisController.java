package com.socialsentiment.backend.analysis;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analyses")
@CrossOrigin(origins = "${app.cors-origin:http://localhost:4000}")
public class InstagramAnalysisController {
    private final AnalysisService service;

    public InstagramAnalysisController(AnalysisService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnalysisDto.Response analyze(@Valid @RequestBody AnalysisDto.Request request) {
        return service.analyze(request);
    }

    @PostMapping("/{id}/reanalyze")
    @ResponseStatus(HttpStatus.CREATED)
    public AnalysisDto.Response reanalyze(@PathVariable UUID id) {
        return service.reanalyzeWithAi(id);
    }

    @GetMapping("/{id}")
    public AnalysisDto.Response get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping
    public List<AnalysisDto.Response> recent() {
        return service.recent();
    }
}
