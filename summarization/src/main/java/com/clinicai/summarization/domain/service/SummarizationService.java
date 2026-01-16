package com.clinicai.summarization.domain.service;

import com.clinicai.summarization.domain.model.Summary;
import com.clinicai.summarization.domain.port.SummaryRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class SummarizationService {
    
    private static final Logger logger = LoggerFactory.getLogger(SummarizationService.class);
    private static final String SYSTEM_PROMPT = "You are a medical AI assistant. Summarize the following clinical encounter text in a concise, professional manner suitable for medical records. Focus on key findings, diagnoses, treatments, and follow-up actions. Keep the summary under 200 words and maintain medical terminology accuracy.";
    
    private final ChatLanguageModel chatModel;
    private final SummaryRepository summaryRepository;
    
    public SummarizationService(ChatLanguageModel chatModel, SummaryRepository summaryRepository) {
        this.chatModel = chatModel;
        this.summaryRepository = summaryRepository;
    }
    
    @Timed(value = "summarization.process.time", description = "Time taken to process summarization")
    public CompletableFuture<Summary> summarizeAsync(String encounterId, String patientId, String clinicianId, String originalText) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            
            Summary summary = new Summary(encounterId, patientId, clinicianId, originalText);
            summary = summaryRepository.save(summary);
            
            summary.setStatus("PROCESSING");
            summaryRepository.save(summary);
            
            try {
                String prompt = SYSTEM_PROMPT + "\n\nClinical Text: " + originalText;
                String aiSummary = chatModel.generate(prompt);
                
                summary.setSummary(aiSummary);
                summary.setStatus("COMPLETED");
                summary.setProcessedAt(LocalDateTime.now());
                summary.setProcessingTimeMs(System.currentTimeMillis() - startTime);
                
                logger.info("Successfully summarized encounter {} in {}ms", 
                    encounterId, summary.getProcessingTimeMs());
                
            } catch (Exception e) {
                summary.setStatus("FAILED");
                summary.setErrorMessage("Summarization failed: " + e.getMessage());
                summary.setProcessingTimeMs(System.currentTimeMillis() - startTime);
                
                logger.error("Failed to summarize encounter {}: {}", encounterId, e.getMessage(), e);
            }
            
            return summaryRepository.save(summary);
        });
    }
    
    @Timed(value = "summarization.retrieve.time", description = "Time taken to retrieve summary")
    public Summary getSummary(String summaryId) {
        return summaryRepository.findById(summaryId)
            .orElseThrow(() -> new IllegalArgumentException("Summary not found: " + summaryId));
    }
    
    public Summary getSummaryByEncounterId(String encounterId) {
        return summaryRepository.findByEncounterId(encounterId)
            .orElseThrow(() -> new IllegalArgumentException("Summary not found for encounter: " + encounterId));
    }
}