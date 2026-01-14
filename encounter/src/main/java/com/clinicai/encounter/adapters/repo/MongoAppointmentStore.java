package com.clinicai.encounter.adapters.repo;

import com.clinicai.encounter.domain.model.Appointment;
import com.clinicai.encounter.domain.port.AppointmentStorePort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MongoAppointmentStore implements AppointmentStorePort {
    private final AppointmentRepository repo;

    public MongoAppointmentStore(AppointmentRepository repo) {
        this.repo = repo;
    }

    @Override
    public Appointment save(Appointment appointment) {
        return repo.save(appointment);
    }

    @Override
    public Optional<Appointment> findById(String id) {
        return repo.findById(id);
    }
}

