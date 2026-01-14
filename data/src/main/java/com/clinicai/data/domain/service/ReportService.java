package com.clinicai.data.domain.service;

import com.clinicai.data.domain.model.Report;
import com.clinicai.data.domain.port.ReportPort;

import java.util.Optional;

public class ReportService {
    private final ReportPort reports;

    public ReportService(ReportPort reports) {
        this.reports = reports;
    }

    public void handleEncounterStarted(String encounterId, String clinicianId, String patientId) {
        Report report = new Report(encounterId, patientId, clinicianId);
        reports.save(report);
    }

    public void handleLabResult(String encounterId, String labPayload) {
        Report report = reports.findByEncounterId(encounterId)
                .orElseGet(() -> new Report(encounterId, null, null));
        report.setLabPayload(labPayload);
        maybeFinalize(report);
        reports.save(report);
    }

    public void uploadRecording(String encounterId, String uri, Double durationSec, String format) {
        Report report = reports.findByEncounterId(encounterId)
                .orElseGet(() -> new Report(encounterId, null, null));
        report.setRecording(uri, durationSec, format);
        maybeFinalize(report);
        reports.save(report);
    }

    public Optional<Report> getReport(String encounterId) {
        return reports.findByEncounterId(encounterId);
    }

    public java.util.List<Report> findByPatient(String patientId) {
        return reports.findByPatientId(patientId);
    }

    private void maybeFinalize(Report report) {
        if (report.getStatus() == com.clinicai.data.domain.model.ReportStatus.READY) return;
        if (report.hasRecording() && report.hasLabPayload()) {
            String analysis = """
                    Auto-generated analysis for encounter %s:
                    - Recording URI: %s
                    - Lab payload: %s
                    """.formatted(
                    report.getEncounterId(),
                    report.getRecordingUri(),
                    report.getLabPayload()
            );
            report.markReady(analysis);
        }
    }
}
