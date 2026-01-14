package com.clinicai.billing.domain.service;

import com.clinicai.billing.app.Clock;
import com.clinicai.billing.app.EventPublisher;
import com.clinicai.billing.domain.model.Invoice;
import com.clinicai.billing.domain.port.InvoiceStorePort;
import com.clinicai.billing.domain.port.PaymentGatewayPort;
import com.clinicai.billing.domain.port.PaymentPort;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

// domain/service/PaymentService.java
public class PaymentService implements PaymentPort {
    private final InvoiceStorePort invoices;
    private final EventPublisher events;
    private final Clock clock;
    private final PaymentGatewayPort gateway;

    public PaymentService(InvoiceStorePort invoices,
                          EventPublisher events,
                          Clock clock,
                          PaymentGatewayPort gateway) {
        this.invoices = invoices;
        this.events = events;
        this.clock = clock;
        this.gateway = gateway;
    }

    @Override public Invoice authorize(String invoiceId, String method) {
        var inv = invoices.findById(invoiceId)
            .orElseThrow(() -> new NoSuchElementException("invoice not found"));
        var now = clock.now();
        if (inv.getExpiresAt() != null && now.isAfter(inv.getExpiresAt())) {
            inv.markFailed();
            invoices.save(inv);
            throw new IllegalStateException("invoice expired");
        }
        boolean authorized = gateway.authorize(inv, method);
        if (!authorized) {
            inv.markFailed();
            invoices.save(inv);
            throw new IllegalStateException("payment authorization failed");
        }
        inv.markPaid();
        invoices.save(inv);
        events.publish("billing.payment.authorized", Map.of(
            "invoiceId", inv.getInvoiceId(), "subjectType", inv.getSubjectType().name(),
            "subjectId", inv.getSubjectId(), "amountCents", inv.getAmountCents(),
            "currency", inv.getCurrency(), "authorizedAt", now.toString()
        ));
        return inv;
    }
}
