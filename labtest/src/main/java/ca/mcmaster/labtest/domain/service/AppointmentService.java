package ca.mcmaster.labtest.domain.service;

import ca.mcmaster.labtest.app.Clock;
import ca.mcmaster.labtest.app.EventPublisher;
import ca.mcmaster.labtest.app.IdGenerator;
import ca.mcmaster.labtest.adapters.repo.AccountRepository;
import ca.mcmaster.labtest.adapters.repo.AccountView;
import ca.mcmaster.labtest.adapters.repo.AppointmentRepository;
import ca.mcmaster.labtest.domain.model.Appointment;
import ca.mcmaster.labtest.domain.model.AppointmentStatus;
import ca.mcmaster.labtest.domain.model.Slot;
import ca.mcmaster.labtest.domain.port.AppointmentPort;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class AppointmentService implements AppointmentPort {
    private static final Duration DEFAULT_PAYMENT_WINDOW = Duration.ofMinutes(10);

    private final Map<String, Slot> slotIndex;           // slotId -> Slot
    private final Map<String, String> bookedSlots;       // slotId -> appointmentId
    private final AppointmentRepository appointments;
    private final AccountRepository accounts;
    private final IdGenerator ids;
    private final EventPublisher events;
    private final Clock clock;
    private final Duration paymentWindow;

    public AppointmentService(Map<String, Slot> slotIndex,
                              Map<String, String> bookedSlots,
                              AppointmentRepository appointments,
                              AccountRepository accounts,
                              IdGenerator ids,
                              EventPublisher events,
                              Clock clock) {
        this(slotIndex, bookedSlots, appointments, accounts, ids, events, clock, DEFAULT_PAYMENT_WINDOW);
    }

    public AppointmentService(Map<String, Slot> slotIndex,
                              Map<String, String> bookedSlots,
                              AppointmentRepository appointments,
                              AccountRepository accounts,
                              IdGenerator ids,
                              EventPublisher events,
                              Clock clock,
                              Duration paymentWindow) {
        this.slotIndex = slotIndex;
        this.bookedSlots = bookedSlots;
        this.appointments = appointments;
        this.accounts = accounts;
        this.ids = ids;
        this.events = events;
        this.clock = clock;
        this.paymentWindow = paymentWindow == null ? DEFAULT_PAYMENT_WINDOW : paymentWindow;
    }

    @Override
    public Appointment book(String labTechnicianId, String patientId, String slotId) {
        Slot slot = Optional.ofNullable(slotIndex.get(slotId))
                .orElseThrow(() -> new NoSuchElementException("slot not found"));
        if (clock.now().isAfter(slot.start())) throw new IllegalStateException("slot in the past");
        if (bookedSlots.containsKey(slotId)) throw new IllegalStateException("slot already booked");

        String apptId = ids.newId();
        Instant paymentDueAt = clock.now().plus(paymentWindow);
        Appointment appointment = new Appointment(apptId, labTechnicianId, patientId, slotId,
                slot.start(), slot.end(), paymentDueAt);

        appointments.save(appointment);
        bookedSlots.put(slotId, apptId);

        // labtest-specific appointment booked event so Billing does not treat it as an Encounter
        events.publish("labtest.appointment.booked", Map.of(
                "appointmentId", apptId,
                "labTechnicianId", labTechnicianId,
                "patientId", patientId,
                "slotId", slotId,
                "start", slot.start().toString(),
                "end", slot.end().toString(),
                "paymentDueAt", paymentDueAt.toString()
        ));
        return appointment;
    }

    @Override
    public Appointment cancel(String appointmentId) {
        Appointment appointment = appointments.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("appointment not found"));
        if (appointment.status() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("appointment already completed");
        }
        appointment.cancel();
        bookedSlots.remove(appointment.slotId());

        appointments.save(appointment);
        return appointment;
    }

    @Override
    public Appointment confirm(String appointmentId, String invoiceId, Instant confirmedAt) {
        Appointment appointment = appointments.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("appointment not found"));
        if (confirmedAt.isAfter(appointment.paymentDueAt())) {
            throw new IllegalStateException("payment window expired");
        }
        appointment.confirm(invoiceId, confirmedAt);
        // Look up labTechnician account to get email address
        String labTechnicianEmail = accounts.findById(appointment.labTechnicianId())
                .map(AccountView::getEmail)
                .orElse(null);

        if (labTechnicianEmail != null && !labTechnicianEmail.isBlank()) {
            String body = "Lab test appointment confirmed for lab technician %s with patient %s at %s – %s."
                    .formatted(
                            appointment.labTechnicianId(),
                            appointment.patientId(),
                            appointment.start().toString(),
                            appointment.end().toString()
                    );

            events.publish("notification.email.labtest.confirmed", Map.of(
                    "to", labTechnicianEmail,
                    "subject", "Lab test appointment confirmed",
                    "body", body
            ));
        }
        appointments.save(appointment);
        return appointment;
    }

    @Override
    public Optional<Appointment> get(String id) {
        return appointments.findById(id);
    }
}
