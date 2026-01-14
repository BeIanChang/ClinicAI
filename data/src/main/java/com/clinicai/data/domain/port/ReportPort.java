package com.clinicai.data.domain.port;

import com.clinicai.data.domain.model.Report;

import java.util.Optional;
import java.util.List;

public interface ReportPort {
    Report save(Report report);
    Optional<Report> findByEncounterId(String encounterId);
    List<Report> findByPatientId(String patientId);
}
