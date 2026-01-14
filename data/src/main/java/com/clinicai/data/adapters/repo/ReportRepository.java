package com.clinicai.data.adapters.repo;

import com.clinicai.data.domain.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends MongoRepository<Report, String> {
    Optional<Report> findByEncounterId(String encounterId);
    List<Report> findByPatientId(String patientId);
}
