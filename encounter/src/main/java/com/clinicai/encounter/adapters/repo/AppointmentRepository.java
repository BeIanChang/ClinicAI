package com.clinicai.encounter.adapters.repo;

import com.clinicai.encounter.domain.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
}

