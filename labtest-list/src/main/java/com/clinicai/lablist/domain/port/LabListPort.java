package com.clinicai.lablist.domain.port;

import com.clinicai.lablist.domain.model.LabList;

import java.util.List;
import java.util.Optional;

public interface LabListPort {
    LabList save(LabList labList);
    Optional<LabList> findByEncounterId(String encounterId);
    Optional<LabList> findById(String id);
    List<LabList> findByPatientId(String patientId);
}
