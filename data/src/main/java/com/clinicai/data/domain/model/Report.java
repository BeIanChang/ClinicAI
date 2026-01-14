package com.clinicai.data.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("reports")
public class Report {
    @Id
    private String id;
    private String encounterId;
    private String patientId;
    private String clinicianId;
    private String payload;
    // Optional raw inputs that underpin the analysis
    private String recordingUri;
    private Double recordingDurationSec;
    private String recordingFormat;
    private String labPayload;
    private ReportStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public Report(String encounterId, String patientId, String clinicianId) {
        this.encounterId = encounterId;
        this.patientId = patientId;
        this.clinicianId = clinicianId;
        this.status = ReportStatus.PROCESSING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getId() { return id; }
    public String getEncounterId() { return encounterId; }
    public String getPatientId() { return patientId; }
    public String getClinicianId() { return clinicianId; }
    public String getPayload() { return payload; }
    public String getRecordingUri() { return recordingUri; }
    public Double getRecordingDurationSec() { return recordingDurationSec; }
    public String getRecordingFormat() { return recordingFormat; }
    public String getLabPayload() { return labPayload; }
    public ReportStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void markReady(String payload) {
        this.payload = payload;
        this.status = ReportStatus.READY;
        this.updatedAt = Instant.now();
    }

    public void markFailed(String message) {
        this.payload = message;
        this.status = ReportStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public void setRecording(String uri, Double durationSec, String format) {
        this.recordingUri = uri;
        this.recordingDurationSec = durationSec;
        this.recordingFormat = format;
        this.updatedAt = Instant.now();
    }

    public void setLabPayload(String labPayload) {
        this.labPayload = labPayload;
        this.updatedAt = Instant.now();
    }

    public boolean hasRecording() {
        return recordingUri != null && recordingFormat != null;
    }

    public boolean hasLabPayload() {
        return labPayload != null;
    }
}
