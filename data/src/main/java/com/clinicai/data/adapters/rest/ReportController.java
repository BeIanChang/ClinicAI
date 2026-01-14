package com.clinicai.data.adapters.rest;

import com.clinicai.data.domain.model.Report;
import com.clinicai.data.domain.service.ReportService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reports;

    public ReportController(ReportService reports) {
        this.reports = reports;
    }

    public record RecordingIn(@NotBlank String uri,
                              double durationSec,
                              @NotBlank String format) {}

    public record LabIn(@NotBlank String payload) {}

    @GetMapping("/{encounterId}")
    public ResponseEntity<Report> get(@PathVariable String encounterId) {
        return reports.getReport(encounterId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Report> byPatient(@RequestParam String patientId) {
        return reports.findByPatient(patientId);
    }

    @PostMapping("/{encounterId}/recording")
    public ResponseEntity<Report> uploadRecording(@PathVariable String encounterId,
                                                  @RequestBody RecordingIn in) {
        reports.uploadRecording(encounterId, in.uri(), in.durationSec(), in.format());
        return get(encounterId);
    }

    @PostMapping("/{encounterId}/lab")
    public ResponseEntity<Report> uploadLab(@PathVariable String encounterId,
                                            @RequestBody LabIn in) {
        reports.handleLabResult(encounterId, in.payload());
        return get(encounterId);
    }
}
