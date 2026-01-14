package ca.mcmaster.labtest.adapters.repo;

import ca.mcmaster.labtest.domain.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
}
