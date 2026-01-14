package com.clinicai.billing.domain.port;

import com.clinicai.billing.domain.model.Invoice;

public interface PaymentPort {
    Invoice authorize(String invoiceId, String method); // returns updated invoice
}