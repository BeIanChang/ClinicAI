package ca.mcmaster.labtest.adapters.rest.dto;

public class AppointmentDtos {
    public record CreateIn(String slotId, String patientId) {}
    public record Out(
            String appointmentId,
            String labTechnicianId,
            String patientId,
            String slotId,
            String status,
            String start,
            String end,
            String paymentDueAt,
            String invoiceId,
            String confirmedAt
    ) {}
}
