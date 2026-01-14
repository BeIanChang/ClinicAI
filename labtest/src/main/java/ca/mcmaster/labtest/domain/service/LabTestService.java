package ca.mcmaster.labtest.domain.service;

import ca.mcmaster.labtest.app.Clock;
import ca.mcmaster.labtest.app.EventPublisher;
import ca.mcmaster.labtest.app.IdGenerator;
import ca.mcmaster.labtest.adapters.repo.AppointmentRepository;
import ca.mcmaster.labtest.domain.model.Appointment;
import ca.mcmaster.labtest.domain.model.AppointmentStatus;
import ca.mcmaster.labtest.domain.model.LabTest;
import ca.mcmaster.labtest.domain.model.TestResultMetadata;
import ca.mcmaster.labtest.domain.port.LabTestPort;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LabTestService implements LabTestPort {
    private final AppointmentRepository appointments;
    private final Map<String, LabTest> labTests = new ConcurrentHashMap<>();
    private final IdGenerator ids;
    private final EventPublisher events;
    private final Clock clock;

    public LabTestService(AppointmentRepository appointments,
                          IdGenerator ids,
                          EventPublisher events,
                          Clock clock) {
        this.appointments = appointments;
        this.ids = ids;
        this.events = events;
        this.clock = clock;
    }

    @Override
    public LabTest start(String appointmentId) {
        Appointment a = appointments.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("appointment not found"));
        if (a.status() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("appointment not confirmed");
        }
        String id = ids.newId();
        var labTest = new LabTest(id, a.appointmentId(), a.patientId(), a.labTechnicianId(), clock.now());
        labTests.put(id, labTest);
        events.publish("labtest.started", Map.of(
                "labTestId", id,
                "appointmentId", a.appointmentId(),
                "patientId", a.patientId(),
                "labTechnicianId", a.labTechnicianId(),
                "startedAt", labTest.startedAt().toString()
        ));
        return labTest;
    }

    @Override
    public LabTest end(String labTestId, TestResultMetadata result) {
        var labTest = Optional.ofNullable(labTests.get(labTestId))
                .orElseThrow(() -> new NoSuchElementException("labtest not found"));
        labTest.end(result, clock.now());
        appointments.findById(labTest.appointmentId()).ifPresent(appt -> {
            appt.complete();
            appointments.save(appt);
        });
        events.publish("lab.tests.result", Map.of(
                "labTestId", labTest.labTestId(),
                "appointmentId", labTest.appointmentId(),
                "patientId", labTest.patientId(),
                "labTechnicianId", labTest.labTechnicianId(),
        "testResult", Map.of(
            "uri", result.uri(),
            "testType", result.testType(),
            "status", result.status(),
            "doctorReport", result.doctorReport()
        ),
                "endedAt", labTest.endedAt().toString()
        ));
        return labTest;
    }

    @Override
    public Optional<LabTest> get(String id) {
        return Optional.ofNullable(labTests.get(id));
    }
}
