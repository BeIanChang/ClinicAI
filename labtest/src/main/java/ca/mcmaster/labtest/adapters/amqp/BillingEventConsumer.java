package ca.mcmaster.labtest.adapters.amqp;

import ca.mcmaster.labtest.app.Clock;
import ca.mcmaster.labtest.app.EventPublisher;
import ca.mcmaster.labtest.domain.port.AppointmentPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class BillingEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(BillingEventConsumer.class);

    private final AppointmentPort appointments;
    private final Clock clock;
    private final EventPublisher events;

    public BillingEventConsumer(AppointmentPort appointments, Clock clock, EventPublisher events) {
        this.appointments = appointments;
        this.clock = clock;
        this.events = events;
    }

    @RabbitListener(queues = RabbitConfig.BILLING_QUEUE)
    public void onEvent(Map<String, Object> payload,
                        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        if (!RabbitConfig.BILLING_PAYMENT_KEY.equals(routingKey)) {
            return;
        }
        String subjectType = (String) payload.get("subjectType");
        if (subjectType == null || !"LabTest".equalsIgnoreCase(subjectType)) {
            return;
        }
        String appointmentId = (String) payload.get("subjectId");
        String invoiceId = (String) payload.get("invoiceId");
        if (appointmentId == null || invoiceId == null) {
            log.warn("billing event missing subjectId or invoiceId: {}", payload);
            publishError("missing appointmentId/invoiceId for billing.payment.authorized", payload);
            return;
        }
        try {
            appointments.confirm(appointmentId, invoiceId, clock.now());
        } catch (IllegalStateException | NoSuchElementException ex) {
            log.warn("skipping billing event for appointment {}: {}", appointmentId, ex.getMessage());
            publishError(ex.getMessage(), payload);
        }
    }

    private void publishError(String reason, Map<String,Object> payload) {
        String safeReason = (reason == null || reason.isBlank()) ? "unknown" : reason;
        events.publish("error.labtest.billing", Map.of(
            "reason", safeReason,
            "payload", payload,
            "service", "labtest"
        ));
    }
}
