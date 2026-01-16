package com.clinicai.summarization.domain.port;

import com.clinicai.summarization.domain.model.Summary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SummaryRepository extends MongoRepository<Summary, String> {
    Optional<Summary> findByEncounterId(String encounterId);
    boolean existsByEncounterId(String encounterId);
}