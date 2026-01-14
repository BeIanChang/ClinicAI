package com.clinicai.lablist.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document("lab_lists")
public class LabList {
    @Id
    private String id;
    private String encounterId;
    private String clinicianId;
    private String patientId;
    private List<String> tests;
    private Instant createdAt;
    private Instant updatedAt;

    public LabList(String encounterId, String clinicianId, String patientId) {
        this.encounterId = encounterId;
        this.clinicianId = clinicianId;
        this.patientId = patientId;
        this.tests = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getId() { return id; }
    public String getEncounterId() { return encounterId; }
    public String getClinicianId() { return clinicianId; }
    public String getPatientId() { return patientId; }
    public List<String> getTests() { return tests; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setTests(List<String> tests) {
        this.tests = new ArrayList<>(tests);
        this.updatedAt = Instant.now();
    }
}
