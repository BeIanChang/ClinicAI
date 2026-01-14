package com.clinicai.data.adapters.repo;

import com.clinicai.data.domain.model.Report;
import com.clinicai.data.domain.port.ReportPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReportMongoAdapter implements ReportPort {
    private final ReportRepository repo;

    public ReportMongoAdapter(ReportRepository repo) {
        this.repo = repo;
    }

    @Override
    public Report save(Report report) {
        return repo.save(report);
    }

    @Override
    public Optional<Report> findByEncounterId(String encounterId) {
        return repo.findByEncounterId(encounterId);
    }

    @Override
    public List<Report> findByPatientId(String patientId) {
        return repo.findByPatientId(patientId);
    }
}
