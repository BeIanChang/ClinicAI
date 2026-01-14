package com.clinicai.encounter.domain.port;

import com.clinicai.encounter.domain.model.Appointment;

import java.time.Instant;
import java.util.Optional;

public interface AppointmentPort {
    Appointment book(String clinicianId, String patientId, String slotId);
    Appointment cancel(String appointmentId);
    Appointment confirm(String appointmentId, String invoiceId, Instant confirmedAt);
    Optional<Appointment> get(String appointmentId);
}
