package com.clinicai.summarization.adapters.rest;

import com.clinicai.summarization.domain.model.Summary;
import com.clinicai.summarization.domain.service.SummarizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/summaries")
@Tag(name = "Summarization", description = "AI-powered clinical summarization APIs")
public class SummarizationController {
    
    private final SummarizationService summarizationService;
    
    public SummarizationController(SummarizationService summarizationService) {
        this.summarizationService = summarizationService;
    }
    
    @PostMapping("/generate")
    @Operation(summary = "Generate AI summary", description = "Asynchronously generates an AI-powered summary of clinical encounter text")
    @Timed(value = "api.summaries.generate.time", description = "Time taken by generate summary API")
    public CompletableFuture<ResponseEntity<Summary>> generateSummary(
            @Parameter(description = "Summarization request details", required = true)
            @RequestBody SummarizationRequest request) {
        
        return summarizationService.summarizeAsync(
            request.getEncounterId(),
            request.getPatientId(),
            request.getClinicianId(),
            request.getOriginalText()
        ).thenApply(ResponseEntity::ok);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get summary by ID", description = "Retrieves a specific summary by its unique identifier")
    @Timed(value = "api.summaries.get.time", description = "Time taken by get summary API")
    public ResponseEntity<Summary> getSummary(
            @Parameter(description = "Summary ID", required = true)
            @PathVariable String id) {
        
        try {
            Summary summary = summarizationService.getSummary(id);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/encounter/{encounterId}")
    @Operation(summary = "Get summary by encounter ID", description = "Retrieves the summary for a specific encounter")
    @Timed(value = "api.summaries.getByEncounter.time", description = "Time taken by get summary by encounter API")
    public ResponseEntity<Summary> getSummaryByEncounterId(
            @Parameter(description = "Encounter ID", required = true)
            @PathVariable String encounterId) {
        
        try {
            Summary summary = summarizationService.getSummaryByEncounterId(encounterId);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    public static class SummarizationRequest {
        private String encounterId;
        private String patientId;
        private String clinicianId;
        private String originalText;
        
        // Getters and setters
        public String getEncounterId() { return encounterId; }
        public void setEncounterId(String encounterId) { this.encounterId = encounterId; }
        
        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }
        
        public String getClinicianId() { return clinicianId; }
        public void setClinicianId(String clinicianId) { this.clinicianId = clinicianId; }
        
        public String getOriginalText() { return originalText; }
        public void setOriginalText(String originalText) { this.originalText = originalText; }
    }
}