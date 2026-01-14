package com.clinicai.lablist.domain.service;

import com.clinicai.lablist.domain.model.LabList;
import com.clinicai.lablist.domain.port.LabListPort;

import java.util.List;
import java.util.Optional;

public class LabListService {
    private final LabListPort port;

    public LabListService(LabListPort port) {
        this.port = port;
    }

    public LabList createIfMissing(String encounterId, String clinicianId, String patientId) {
        return port.findByEncounterId(encounterId)
                .orElseGet(() -> port.save(new LabList(encounterId, clinicianId, patientId)));
    }

    public LabList updateTests(String encounterId, List<String> tests) {
        LabList labList = port.findByEncounterId(encounterId)
                .orElseThrow(() -> new IllegalArgumentException("lab list not found"));
        labList.setTests(tests);
        return port.save(labList);
    }

    public Optional<LabList> getByEncounterId(String encounterId) { return port.findByEncounterId(encounterId); }
    public Optional<LabList> getById(String id) { return port.findById(id); }
    public List<LabList> getByPatientId(String patientId) { return port.findByPatientId(patientId); }
}
