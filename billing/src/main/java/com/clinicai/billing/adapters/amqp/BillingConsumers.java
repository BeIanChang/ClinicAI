// com.clinicai.billing.adapters.amqp.BillingConsumers.java
package com.clinicai.billing.adapters.amqp;

import com.clinicai.billing.app.Clock;
import com.clinicai.billing.app.EventPublisher;
import com.clinicai.billing.domain.model.SubjectType;
import com.clinicai.billing.domain.port.InvoicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Component
public class BillingConsumers {
    private static final Logger log = LoggerFactory.getLogger(BillingConsumers.class);
    private final InvoicePort invoices;
    private final Clock clock;
    private final EventPublisher events;

    public BillingConsumers(InvoicePort invoices, Clock clock, EventPublisher events) {
        this.invoices = invoices;
        this.clock = clock;
        this.events = events;
    }

    // declare durable queue + bindings at startup
    @Bean
    public Declarables billingBindings() {
        Queue appointmentQueue = new Queue(RabbitConfig.APPOINTMENT_BOOKED_QUEUE, true);
        TopicExchange ex = new TopicExchange(RabbitConfig.EXCHANGE, true, false);
        return new Declarables(
            appointmentQueue,
            ex,
            // Encounter appointments
            BindingBuilder.bind(appointmentQueue).to(ex).with("appointment.booked"),
            // LabTest appointments
            BindingBuilder.bind(appointmentQueue).to(ex).with("labtest.appointment.booked")
        );
    }

    @RabbitListener(queues = RabbitConfig.APPOINTMENT_BOOKED_QUEUE)
    public void onEvent(Map<String,Object> event,
                        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key) {
        log.info("[Billing] received event key={} payload={}", key, event);
        switch (key) {
            case "appointment.booked" -> handleEncounterAppointmentBooked(event);
            case "labtest.appointment.booked" -> handleLabTestAppointmentBooked(event);
            default -> { /* ignore */ }
        }
    }

    private void handleEncounterAppointmentBooked(Map<String,Object> e) {
        String appointmentId = (String) e.get("appointmentId");
        Instant expiresAt = parseInstant((String) e.get("paymentDueAt"));
        if (expiresAt == null) {
            expiresAt = clock.now().plus(Duration.ofMinutes(10));
        }
        if (appointmentId == null || appointmentId.isBlank()) {
            publishError("error.billing.invoice", "missing appointmentId on appointment.booked", e);
            return;
        }
        try {
            invoices.create(SubjectType.Encounter, appointmentId, 8000L, "CAD", expiresAt);
        } catch (RuntimeException ex) {
            publishError("error.billing.invoice", ex.getMessage(), e);
        }
    }

    private void handleLabTestAppointmentBooked(Map<String,Object> e) {
        String appointmentId = (String) e.get("appointmentId");
        Instant expiresAt = parseInstant((String) e.get("paymentDueAt"));
        if (expiresAt == null) {
            expiresAt = clock.now().plus(Duration.ofMinutes(10));
        }
        if (appointmentId == null || appointmentId.isBlank()) {
            publishError("error.billing.invoice", "missing appointmentId on labtest.appointment.booked", e);
            return;
        }
        try {
            invoices.create(SubjectType.LabTest, appointmentId, 15000L, "CAD", expiresAt);
        } catch (RuntimeException ex) {
            publishError("error.billing.invoice", ex.getMessage(), e);
        }
    }

    private Instant parseInstant(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return Instant.parse(raw);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private void publishError(String key, String reason, Map<String,Object> payload) {
        String safeReason = (reason == null || reason.isBlank()) ? "unknown" : reason;
        events.publish(key, Map.of(
            "reason", safeReason,
            "payload", payload,
            "service", "billing"
        ));
    }
}
