package com.clinicai.billing.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("invoices")
public class Invoice {
    @Id
    private String invoiceId;
    private SubjectType subjectType;
    private String subjectId;
    private long amountCents;
    private String currency;
    private Instant createdAt;
    private Instant expiresAt;
    private InvoiceStatus status = InvoiceStatus.PENDING;

    public Invoice() {
        // for MongoDB
    }

    public Invoice(String invoiceId, SubjectType subjectType, String subjectId,
                    long amountCents, String currency, Instant createdAt, Instant expiresAt) {
        this.invoiceId = invoiceId;
        this.subjectType = subjectType;
        this.subjectId = subjectId;
        this.amountCents = amountCents;
        this.currency = currency;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public String getInvoiceId() { return invoiceId; }
    public SubjectType getSubjectType() { return subjectType; }
    public String getSubjectId() { return subjectId; }
    public long getAmountCents() { return amountCents; }
    public String getCurrency() { return currency; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public InvoiceStatus getStatus() { return status; }

    public void markPaid() { this.status = InvoiceStatus.PAID; }
    public void markFailed() { this.status = InvoiceStatus.FAILED; }
}
