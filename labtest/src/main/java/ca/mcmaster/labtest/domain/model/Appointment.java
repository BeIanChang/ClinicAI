package ca.mcmaster.labtest.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

// Stored in clinicai-data DB, separate collection from Encounter appointments
@Document("labtest_appointments")
public final class Appointment {
    @Id
    private final String appointmentId;
    private final String labTechnicianId;
    private final String patientId;
    private final String slotId;
    private final Instant start;
    private final Instant end;
    private final Instant paymentDueAt;
    private AppointmentStatus status;
    private String invoiceId;
    private Instant confirmedAt;

    public Appointment(String appointmentId,
                       String labTechnicianId,
                       String patientId,
                       String slotId,
                       Instant start,
                       Instant end,
                       Instant paymentDueAt) {
        this.appointmentId = appointmentId;
        this.labTechnicianId = labTechnicianId;
        this.patientId = patientId;
        this.slotId = slotId;
        this.start = start;
        this.end = end;
        this.paymentDueAt = paymentDueAt;
        this.status = AppointmentStatus.PENDING_PAYMENT;
    }

    public String appointmentId() { return appointmentId; }
    public String labTechnicianId() { return labTechnicianId; }
    public String patientId() { return patientId; }
    public String slotId() { return slotId; }
    public Instant start() { return start; }
    public Instant end() { return end; }
    public Instant paymentDueAt() { return paymentDueAt; }
    public AppointmentStatus status() { return status; }
    public String invoiceId() { return invoiceId; }
    public Instant confirmedAt() { return confirmedAt; }

    public void cancel() { this.status = AppointmentStatus.CANCELLED; }
    public void complete() { this.status = AppointmentStatus.COMPLETED; }

    public void confirm(String invoiceId, Instant confirmedAt) {
        if (status != AppointmentStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("appointment not awaiting payment");
        }
        this.invoiceId = invoiceId;
        this.confirmedAt = confirmedAt;
        this.status = AppointmentStatus.CONFIRMED;
    }
}
