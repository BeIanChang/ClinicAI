package com.clinicai.encounter.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("appointments")
public class Appointment {
    @Id
    private String appointmentId;
    private String clinicianId;
    private String patientId;
    private String slotId;
    private Instant start;
    private Instant end;
    private Instant paymentDueAt;
    private AppointmentStatus status;
    private String invoiceId;
    private Instant confirmedAt;

    public Appointment() {
        // for MongoDB
    }

    public Appointment(String id,
                       String clinicianId,
                       String patientId,
                       String slotId,
                       Instant start,
                       Instant end,
                       Instant paymentDueAt) {
        this.appointmentId = id;
        this.clinicianId = clinicianId;
        this.patientId = patientId;
        this.slotId = slotId;
        this.start = start;
        this.end = end;
        this.paymentDueAt = paymentDueAt;
        this.status = AppointmentStatus.PENDING_PAYMENT;
    }

    public String appointmentId() { return appointmentId; }
    public String clinicianId() { return clinicianId; }
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

    public void startEncounter() { this.status = AppointmentStatus.IN_PROGRESS; }

    public void confirm(String invoiceId, Instant confirmedAt) {
        if (status != AppointmentStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("appointment not awaiting payment");
        }
        this.invoiceId = invoiceId;
        this.confirmedAt = confirmedAt;
        this.status = AppointmentStatus.CONFIRMED;
    }
}
