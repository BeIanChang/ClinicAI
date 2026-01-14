package com.clinicai.data.adapters.http;

import com.clinicai.data.domain.model.Report;
import com.clinicai.data.domain.service.ReportService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/partner-callback")
public class PartnerCallbackController {
    private final ReportService reports;

    public PartnerCallbackController(ReportService reports) {
        this.reports = reports;
    }

    public record ReportIn(@NotBlank String encounterId,
                           String patientId,
                           String clinicianId,
                           @NotBlank String payload) {}

    @PostMapping("/report")
    public ResponseEntity<Map<String, String>> receive(@RequestBody ReportIn in) {
        reports.partnerReportReady(in.encounterId(), in.clinicianId(), in.patientId(), in.payload());
        return ResponseEntity.ok(Map.of("status", "stored"));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() { return ResponseEntity.ok("ok"); }
}
