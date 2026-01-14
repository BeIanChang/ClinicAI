package ca.mcmaster.labtest.domain.port;

import ca.mcmaster.labtest.domain.model.Appointment;
import java.time.Instant;
import java.util.Optional;

public interface AppointmentPort {
    Appointment book(String labTechnicianId, String patientId, String slotId);
    Appointment confirm(String appointmentId, String invoiceId, Instant confirmedAt);
    Appointment cancel(String appointmentId);
    Optional<Appointment> get(String id);
}
