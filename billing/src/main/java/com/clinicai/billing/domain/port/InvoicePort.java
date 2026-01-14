package com.clinicai.billing.domain.port;

import com.clinicai.billing.domain.model.Invoice;
import com.clinicai.billing.domain.model.SubjectType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface InvoicePort {
    Invoice create(SubjectType type, String subjectId, long amountCents, String currency, Instant expiresAt);
    Optional<Invoice> get(String invoiceId);
    List<Invoice> findBySubject(SubjectType type, String subjectId);
}
