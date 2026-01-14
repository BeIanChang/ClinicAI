package ca.mcmaster.labtest.adapters.rest.dto;

public class LabTestDtos {
        public record TestResultIn(String uri, String testType, String status, String doctorReport) {}
    public record LabTestOut(
            String labTestId,
            String appointmentId,
            String patientId,
            String labTechnicianId,
            String startedAt,
            String endedAt
    ) {}
    public record CompletedEventOut(
            String labTestId,
            String patientId,
            TestResultIn testResult,
            String endedAt
    ) {}
}
