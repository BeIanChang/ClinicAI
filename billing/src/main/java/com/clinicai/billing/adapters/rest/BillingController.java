package com.clinicai.billing.adapters.rest;

import com.clinicai.billing.domain.model.Invoice;
import com.clinicai.billing.domain.model.SubjectType;
import com.clinicai.billing.domain.port.InvoicePort;
import com.clinicai.billing.domain.port.PaymentPort;
import com.clinicai.billing.adapters.rest.dto.BillingDtos.CreateInvoiceReq;
import com.clinicai.billing.adapters.rest.dto.BillingDtos.InvoiceOut;
import com.clinicai.billing.adapters.rest.dto.BillingDtos.PayReq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController @RequestMapping("/billing/invoices")
public class BillingController {
    private final InvoicePort invoices; private final PaymentPort payments;

    public BillingController(InvoicePort invoices, PaymentPort payments) { this.invoices = invoices; this.payments = payments; }

    @PostMapping
    public ResponseEntity<InvoiceOut> create(@RequestBody CreateInvoiceReq req) {
        var type = SubjectType.valueOf(req.subjectType());
        Instant expiresAt = req.expiresAt() != null && !req.expiresAt().isBlank()
                ? Instant.parse(req.expiresAt())
                : null;
        var inv = invoices.create(type, req.subjectId(), req.amountCents(), req.currency(), expiresAt);
        return ResponseEntity.status(201).body(toOut(inv));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceOut> get(@PathVariable String id) {
        return invoices.get(id).map(i -> ResponseEntity.ok(toOut(i))).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<InvoiceOut> find(@RequestParam String subjectType, @RequestParam String subjectId) {
        var t = SubjectType.valueOf(subjectType);
        return invoices.findBySubject(t, subjectId).stream().map(this::toOut).toList();
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<InvoiceOut> pay(@PathVariable String id, @RequestBody PayReq req) {
        var inv = payments.authorize(id, req.method());
        return ResponseEntity.ok(toOut(inv));
    }

    private InvoiceOut toOut(Invoice i) {
        return new InvoiceOut(i.getInvoiceId(), i.getSubjectType().name(), i.getSubjectId(),
        i.getAmountCents(), i.getCurrency(), i.getStatus().name(),
        i.getCreatedAt().toString(), i.getExpiresAt().toString());
    }
}
