package com.clinicai.billing.domain.port;

import com.clinicai.billing.domain.model.Invoice;
import com.clinicai.billing.domain.model.SubjectType;

import java.util.List;
import java.util.Optional;

public interface InvoiceStorePort {
    Invoice save(Invoice invoice);
    Optional<Invoice> findById(String id);
    List<Invoice> findBySubject(SubjectType type, String subjectId);
}

