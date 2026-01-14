package com.clinicai.encounter.adapters.repo;

import com.clinicai.encounter.domain.model.Encounter;
import com.clinicai.encounter.domain.port.EncounterStorePort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MongoEncounterStore implements EncounterStorePort {
    private final EncounterRepository repo;

    public MongoEncounterStore(EncounterRepository repo) {
        this.repo = repo;
    }

    @Override
    public Encounter save(Encounter encounter) {
        return repo.save(encounter);
    }

    @Override
    public Optional<Encounter> findById(String id) {
        return repo.findById(id);
    }
}

