package com.clinicai.lablist.adapters.repo;

import com.clinicai.lablist.domain.model.LabList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LabListRepository extends MongoRepository<LabList, String> {
    Optional<LabList> findByEncounterId(String encounterId);
    List<LabList> findByPatientId(String patientId);
}
