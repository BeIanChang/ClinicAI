package com.clinicai.encounter.domain.port;

import com.clinicai.encounter.domain.model.Appointment;

import java.util.Optional;

public interface AppointmentStorePort {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(String id);
}

