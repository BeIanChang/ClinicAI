package ca.mcmaster.labtest.domain.model;

import java.time.Instant;

public final class LabTest {
    private final String labTestId;
    private final String appointmentId;
    private final String patientId;
    private final String labTechnicianId;
    private final Instant startedAt;
    private Instant endedAt;
    private TestResultMetadata testResult;

    public LabTest(String id, String appointmentId, String patientId, String labTechnicianId, Instant startedAt) {
        this.labTestId = id;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.labTechnicianId = labTechnicianId;
        this.startedAt = startedAt;
    }

    public String labTestId() { return labTestId; }
    public String appointmentId() { return appointmentId; }
    public String patientId() { return patientId; }
    public String labTechnicianId() { return labTechnicianId; }
    public Instant startedAt() { return startedAt; }
    public Instant endedAt() { return endedAt; }
    public TestResultMetadata testResult() { return testResult; }

    public void end(TestResultMetadata result, Instant endedAt) {
        this.testResult = result;
        this.endedAt = endedAt;
    }
}
