package com.clinicai.lablist.adapters.amqp;

import com.clinicai.lablist.domain.service.LabListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LabListConsumer {
    private static final Logger log = LoggerFactory.getLogger(LabListConsumer.class);
    private final LabListService labLists;
    private final com.clinicai.lablist.app.EventPublisher events;

    public LabListConsumer(LabListService labLists, com.clinicai.lablist.app.EventPublisher events) {
        this.labLists = labLists;
        this.events = events;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onEvent(Map<String,Object> payload,
                        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key) {
        log.info("[LabTest-List] received event key={} payload={}", key, payload);
        if (!RabbitConfig.ENCOUNTER_COMPLETED.equals(key)) return;
        String encId = (String) payload.get("encounterId");
        String clinicianId = (String) payload.get("clinicianId");
        String patientId = (String) payload.get("patientId");
        if (encId == null || clinicianId == null || patientId == null) {
            log.warn("lab.tests.update missing fields: {}", payload);
            publishError("missing encounterId/clinicianId/patientId", payload);
            return;
        }
        try {
            labLists.createIfMissing(encId, clinicianId, patientId);
        } catch (RuntimeException ex) {
            log.warn("failed to create lab list for {}: {}", encId, ex.getMessage());
            publishError(ex.getMessage(), payload);
        }
    }

    private void publishError(String reason, Map<String,Object> payload) {
        String safeReason = (reason == null || reason.isBlank()) ? "unknown" : reason;
        events.publish("error.lablist.update", Map.of(
            "reason", safeReason,
            "payload", payload,
            "service", "labtest-list"
        ));
    }
}
