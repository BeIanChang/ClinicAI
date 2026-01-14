package com.clinicai.billing.adapters.rest.dto;

// adapters/rest/dto/BillingDtos.java
public class BillingDtos {
    public record CreateInvoiceReq(String subjectType, String subjectId, long amountCents, String currency, String expiresAt) {}
    public record InvoiceOut(String invoiceId, String subjectType, String subjectId,
                            long amountCents, String currency, String status, String createdAt, String expiresAt) {}
    public record PayReq(String method) {}
}
