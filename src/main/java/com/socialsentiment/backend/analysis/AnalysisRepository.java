package com.socialsentiment.backend.analysis;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AnalysisRepository extends JpaRepository<AnalysisEntity, UUID> {}
