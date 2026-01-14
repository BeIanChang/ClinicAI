// domain/service/InvoiceService.java
package com.clinicai.billing.domain.service;

import com.clinicai.billing.app.Clock;
import com.clinicai.billing.app.EventPublisher;
import com.clinicai.billing.app.IdGenerator;
import com.clinicai.billing.domain.model.Invoice;
import com.clinicai.billing.domain.model.SubjectType;
import com.clinicai.billing.domain.port.InvoicePort;
import com.clinicai.billing.domain.port.InvoiceStorePort;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class InvoiceService implements InvoicePort {
    private final InvoiceStorePort invoices;
    private final EventPublisher events;
    private final IdGenerator ids; private final Clock clock;

    public InvoiceService(InvoiceStorePort invoices, EventPublisher events, IdGenerator ids, Clock clock) {
        this.invoices = invoices; this.events=events; this.ids=ids; this.clock=clock;
    }

    @Override public Invoice create(SubjectType type, String subjectId, long amountCents, String currency, Instant expiresAt) {
        String id = ids.newId();
        Instant createdAt = clock.now();
        Instant resolvedExpiry = expiresAt != null ? expiresAt : createdAt.plus(Duration.ofMinutes(10));
        var inv = new Invoice(id, type, subjectId, amountCents, currency, createdAt, resolvedExpiry);
        invoices.save(inv);
        events.publish("billing.invoice.created", Map.of(
          "invoiceId", id, "subjectType", type.name(), "subjectId", subjectId,
          "amountCents", amountCents, "currency", currency, "status", "PENDING",
          "createdAt", createdAt.toString(), "expiresAt", resolvedExpiry.toString()
        ));
        return inv;
    }

    @Override public Optional<Invoice> get(String id) { return invoices.findById(id); }

    @Override public List<Invoice> findBySubject(SubjectType t, String subjectId) {
      return invoices.findBySubject(t, subjectId);
    }
}
