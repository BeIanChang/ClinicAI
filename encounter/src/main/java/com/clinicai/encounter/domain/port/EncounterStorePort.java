package com.clinicai.encounter.domain.port;

import com.clinicai.encounter.domain.model.Encounter;

import java.util.Optional;

public interface EncounterStorePort {
    Encounter save(Encounter encounter);
    Optional<Encounter> findById(String id);
}

