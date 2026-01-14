package com.clinicai.data.adapters.amqp;

import com.clinicai.data.domain.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataConsumers {
    private static final Logger log = LoggerFactory.getLogger(DataConsumers.class);
    private final ReportService reports;
    private final com.clinicai.data.app.EventPublisher events;

    public DataConsumers(ReportService reports, com.clinicai.data.app.EventPublisher events) {
        this.reports = reports;
        this.events = events;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onEvent(Map<String, Object> payload,
                        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key) {
        log.info("[Data] received event key={} payload={}", key, payload);
        switch (key) {
            case RabbitConfig.DATA_ANALYSIS_REQUESTED -> handleDataAnalysisRequested(payload);
            case RabbitConfig.LAB_RESULT -> handleLabResult(payload);
            default -> {}
        }
    }

    private void handleDataAnalysisRequested(Map<String, Object> payload) {
        log.info("[Data] handleDataAnalysisRequested received payload: {}", payload);
        String encId = (String) payload.get("encounterId");
        String clinicianId = (String) payload.get("clinicianId");
        String patientId = (String) payload.get("patientId");
        if (encId == null || patientId == null || clinicianId == null) {
            log.warn("data.analysis.requested missing required fields: {}", payload);
            publishError("missing encounterId/clinicianId/patientId", payload);
            return;
        }
        log.info("[Data] Processing data.analysis.requested - encounterId={}, clinicianId={}, patientId={}", encId, clinicianId, patientId);
        reports.handleEncounterStarted(encId, clinicianId, patientId);
        log.info("[Data] handleDataAnalysisRequested completed for encounterId={}", encId);
    }

    private void handleLabResult(Map<String, Object> payload) {
        log.info("[Data] handleLabResult received payload: {}", payload);
        String encId = (String) payload.get("encounterId");
        String results = (String) payload.get("results");
        if (encId == null || results == null) {
            log.warn("lab result missing encounterId/results: {}", payload);
            publishError("missing encounterId/results for lab.tests.result", payload);
            return;
        }
        log.info("[Data] Processing lab.result - encounterId={}, results={}", encId, results);
        reports.handleLabResult(encId, results);
        log.info("[Data] handleLabResult completed for encounterId={}", encId);
    }

    private void publishError(String reason, Map<String,Object> payload) {
        String safeReason = (reason == null || reason.isBlank()) ? "unknown" : reason;
        events.publish("error.data.report", Map.of(
            "reason", safeReason,
            "payload", payload,
            "service", "data"
        ));
    }
}
