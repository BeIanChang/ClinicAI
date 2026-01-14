package com.clinicai.encounter.domain.service;

import com.clinicai.encounter.app.Clock;
import com.clinicai.encounter.app.IdGenerator;
import com.clinicai.encounter.app.policy.EncounterPolicyHandler;
import com.clinicai.encounter.domain.port.AppointmentStorePort;
import com.clinicai.encounter.domain.model.Appointment;
import com.clinicai.encounter.domain.model.AppointmentStatus;
import com.clinicai.encounter.domain.model.Slot;
import com.clinicai.encounter.domain.port.AppointmentPort;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class AppointmentService implements AppointmentPort {
    private static final Duration DEFAULT_PAYMENT_WINDOW = Duration.ofMinutes(10);

    private final Map<String, Slot> slotIndex;          // slotId -> Slot
    private final Map<String, String> bookedSlots;      // slotId -> appointmentId
    private final AppointmentStorePort appointments;
    private final IdGenerator ids;
    private final Clock clock;
    private final Duration paymentWindow;
    private final EncounterPolicyHandler policies;

    public AppointmentService(Map<String, Slot> slotIndex,
                              Map<String, String> bookedSlots,
                              AppointmentStorePort appointments,
                              IdGenerator ids,
                              Clock clock,
                              EncounterPolicyHandler policies) {
        this(slotIndex, bookedSlots, appointments, ids, clock, DEFAULT_PAYMENT_WINDOW, policies);
    }

    public AppointmentService(Map<String, Slot> slotIndex,
                              Map<String, String> bookedSlots,
                              AppointmentStorePort appointments,
                              IdGenerator ids,
                              Clock clock,
                              Duration paymentWindow,
                              EncounterPolicyHandler policies) {
        this.slotIndex = slotIndex;
        this.bookedSlots = bookedSlots;
        this.appointments = appointments;
        this.ids = ids;
        this.clock = clock;
        this.paymentWindow = paymentWindow == null ? DEFAULT_PAYMENT_WINDOW : paymentWindow;
        this.policies = policies;
    }

    @Override
    public Appointment book(String clinicianId, String patientId, String slotId) {
        Slot slot = Optional.ofNullable(slotIndex.get(slotId))
                .orElseThrow(() -> new NoSuchElementException("slot not found"));
        if (clock.now().isAfter(slot.start())) throw new IllegalStateException("slot in the past");
        if (bookedSlots.containsKey(slotId)) throw new IllegalStateException("slot already booked");

        String apptId = ids.newId();
        Instant paymentDueAt = clock.now().plus(paymentWindow);
        Appointment appointment = new Appointment(apptId, clinicianId, patientId, slotId,
                slot.start(), slot.end(), paymentDueAt);

        appointments.save(appointment);
        bookedSlots.put(slotId, apptId);

        if (policies != null) {
            policies.onAppointmentBooked(appointment);
        }
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
        appointments.save(appointment);
        if (policies != null) {
            policies.onAppointmentConfirmed(appointment);
        }
        return appointment;
    }

    @Override
    public Optional<Appointment> get(String id) {
        return appointments.findById(id);
    }
}
