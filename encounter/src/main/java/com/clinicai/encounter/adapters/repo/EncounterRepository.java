package com.clinicai.encounter.adapters.repo;

import com.clinicai.encounter.domain.model.Encounter;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EncounterRepository extends MongoRepository<Encounter, String> {
}

