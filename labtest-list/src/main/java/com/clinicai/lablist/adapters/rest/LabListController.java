package com.clinicai.lablist.adapters.rest;

import com.clinicai.lablist.domain.model.LabList;
import com.clinicai.lablist.domain.service.LabListService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lablists")
public class LabListController {
    private final LabListService labLists;

    public LabListController(LabListService labLists) {
        this.labLists = labLists;
    }

    public record TestsIn(@NotNull List<String> tests) {}

    @PostMapping("/{encounterId}/tests")
    public ResponseEntity<LabList> setTests(@PathVariable String encounterId, @RequestBody TestsIn in) {
        var updated = labLists.updateTests(encounterId, in.tests());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{encounterId}")
    public ResponseEntity<LabList> get(@PathVariable String encounterId) {
        return labLists.getByEncounterId(encounterId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<LabList> byPatient(@RequestParam(required = false) String patientId) {
        return patientId == null ? List.of() : labLists.getByPatientId(patientId);
    }
}
