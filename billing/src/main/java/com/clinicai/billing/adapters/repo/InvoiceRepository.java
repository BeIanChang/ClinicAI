package com.clinicai.billing.adapters.repo;

import com.clinicai.billing.domain.model.Invoice;
import com.clinicai.billing.domain.model.SubjectType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    List<Invoice> findBySubjectTypeAndSubjectId(SubjectType subjectType, String subjectId);
}

