package com.clinicai.billing.adapters.repo;

import com.clinicai.billing.domain.model.Invoice;
import com.clinicai.billing.domain.model.SubjectType;
import com.clinicai.billing.domain.port.InvoiceStorePort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MongoInvoiceStore implements InvoiceStorePort {
    private final InvoiceRepository repo;

    public MongoInvoiceStore(InvoiceRepository repo) {
        this.repo = repo;
    }

    @Override
    public Invoice save(Invoice invoice) {
        return repo.save(invoice);
    }

    @Override
    public Optional<Invoice> findById(String id) {
        return repo.findById(id);
    }

    @Override
    public List<Invoice> findBySubject(SubjectType type, String subjectId) {
        return repo.findBySubjectTypeAndSubjectId(type, subjectId);
    }
}

