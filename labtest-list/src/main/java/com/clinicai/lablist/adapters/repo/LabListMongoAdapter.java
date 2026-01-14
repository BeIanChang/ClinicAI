package com.clinicai.lablist.adapters.repo;

import com.clinicai.lablist.domain.model.LabList;
import com.clinicai.lablist.domain.port.LabListPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class LabListMongoAdapter implements LabListPort {
    private final LabListRepository repo;

    public LabListMongoAdapter(LabListRepository repo) {
        this.repo = repo;
    }

    @Override public LabList save(LabList labList) { return repo.save(labList); }
    @Override public Optional<LabList> findByEncounterId(String encounterId) { return repo.findByEncounterId(encounterId); }
    @Override public Optional<LabList> findById(String id) { return repo.findById(id); }
    @Override public List<LabList> findByPatientId(String patientId) { return repo.findByPatientId(patientId); }
}
