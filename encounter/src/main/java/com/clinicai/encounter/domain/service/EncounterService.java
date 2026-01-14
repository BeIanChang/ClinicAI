package com.clinicai.encounter.domain.service;

import com.clinicai.encounter.app.Clock;
import com.clinicai.encounter.app.EventPublisher;
import com.clinicai.encounter.app.IdGenerator;
import com.clinicai.encounter.app.policy.EncounterPolicyHandler;
import com.clinicai.encounter.domain.port.AppointmentStorePort;
import com.clinicai.encounter.domain.port.EncounterStorePort;
import com.clinicai.encounter.domain.model.Appointment;
import com.clinicai.encounter.domain.model.AppointmentStatus;
import com.clinicai.encounter.domain.model.Encounter;
import com.clinicai.encounter.domain.port.EncounterPort;

import java.util.NoSuchElementException;
import java.util.Optional;

public class EncounterService implements EncounterPort {
    private final AppointmentStorePort appointments;
    private final EncounterStorePort encounters;
    private final IdGenerator ids;
    private final EventPublisher events;
    private final Clock clock;
    private final EncounterPolicyHandler policies;

    public EncounterService(AppointmentStorePort appointments,
                            EncounterStorePort encounters,
                            IdGenerator ids,
                            EventPublisher events,
                            Clock clock,
                            EncounterPolicyHandler policies) {
        this.appointments = appointments;
        this.encounters = encounters;
        this.ids = ids;
        this.events = events;
        this.clock = clock;
        this.policies = policies;
    }

    @Override
    public Encounter start(String appointmentId) {
        Appointment a = appointments.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException("appointment not found"));
        if (a.status() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("appointment not confirmed");
        }
        a.startEncounter();
        appointments.save(a);
        String id = ids.newId();
        var e = new Encounter(id, a.appointmentId(), a.patientId(), a.clinicianId(), clock.now());
        return encounters.save(e);
    }

    @Override
    public Encounter end(String encounterId) {
        var e = encounters.findById(encounterId)
                .orElseThrow(() -> new NoSuchElementException("not found"));
        e.end(clock.now());
        appointments.findById(e.appointmentId()).ifPresent(a -> {
            a.complete();
            appointments.save(a);
        });
        if (policies != null) {
            policies.onEncounterCompleted(e);
        }
        return encounters.save(e);
    }

    @Override
    public Optional<Encounter> get(String id) {
        return encounters.findById(id);
    }
}
