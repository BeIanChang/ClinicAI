package com.clinicai.encounter.adapters.rest;

import com.clinicai.encounter.adapters.rest.dto.AppointmentDtos;
import com.clinicai.encounter.domain.model.Appointment;
import com.clinicai.encounter.domain.port.AppointmentPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentPort appointments;

    public AppointmentController(AppointmentPort appointments) {
        this.appointments = appointments;
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

        String clinicianId = extractClinicianId(slotId);
        Appointment appointment = appointments.book(clinicianId, patientId, slotId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toOut(appointment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDtos.Out> get(@PathVariable String id) {
        return appointments.get(id)
                .map(appt -> ResponseEntity.ok(toOut(appt)))
                .orElse(ResponseEntity.notFound().build());
    }

    private AppointmentDtos.Out toOut(Appointment appt) {
        return new AppointmentDtos.Out(
                appt.appointmentId(),
                appt.clinicianId(),
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

    private String extractClinicianId(String slotId) {
        int idx = slotId.indexOf('#');
        if (idx < 1) throw new IllegalArgumentException("invalid slotId");
        return slotId.substring(0, idx);
    }
}
