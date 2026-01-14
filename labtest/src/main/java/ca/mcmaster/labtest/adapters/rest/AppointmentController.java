package ca.mcmaster.labtest.adapters.rest;

import ca.mcmaster.labtest.adapters.rest.dto.AppointmentDtos;
import ca.mcmaster.labtest.app.Clock;
import ca.mcmaster.labtest.domain.model.Appointment;
import ca.mcmaster.labtest.domain.port.AppointmentPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentPort appointments;
    private final Clock clock;

    public AppointmentController(AppointmentPort appointments, Clock clock) {
        this.appointments = appointments;
        this.clock = clock;
    }

    @PostMapping
    public ResponseEntity<AppointmentDtos.Out> book(
            @RequestParam(value = "slotId", required = false) String slotIdFromQuery,
            @RequestBody(required = false) AppointmentDtos.CreateIn body) {

        String slotId = slotIdFromQuery != null && !slotIdFromQuery.isBlank()
                ? slotIdFromQuery
                : (body != null ? body.slotId() : null);

        String patientId = body != null ? body.patientId() : null;

        if (slotId == null || slotId.isBlank() || patientId == null || patientId.isBlank()) {
            throw new IllegalArgumentException("slotId and patientId are required");
        }

        String labTechnicianId = extractLabTechnicianId(slotId);
        Appointment appointment = appointments.book(labTechnicianId, patientId, slotId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toOut(appointment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDtos.Out> get(@PathVariable String id) {
        return appointments.get(id)
                .map(appt -> ResponseEntity.ok(toOut(appt)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Confirms an appointment with payment. For testing purposes or direct confirmation.
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<AppointmentDtos.Out> confirm(
            @PathVariable String id,
            @RequestBody ConfirmRequest body) {
        Appointment appointment = appointments.confirm(id, body.invoiceId(), clock.now());
        return ResponseEntity.ok(toOut(appointment));
    }

    public record ConfirmRequest(String invoiceId) {}

    private AppointmentDtos.Out toOut(Appointment appt) {
        return new AppointmentDtos.Out(
                appt.appointmentId(),
                appt.labTechnicianId(),
                appt.patientId(),
                appt.slotId(),
                appt.status().name(),
                appt.start().toString(),
                appt.end().toString(),
                appt.paymentDueAt().toString(),
                appt.invoiceId(),
                appt.confirmedAt() != null ? appt.confirmedAt().toString() : null
        );
    }

    private String extractLabTechnicianId(String slotId) {
        int idx = slotId.indexOf('#');
        if (idx < 1) throw new IllegalArgumentException("invalid slotId");
        return slotId.substring(0, idx);
    }
}
